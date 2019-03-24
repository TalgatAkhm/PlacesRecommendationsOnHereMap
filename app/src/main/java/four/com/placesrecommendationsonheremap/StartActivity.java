package four.com.placesrecommendationsonheremap;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import four.com.placesrecommendationsonheremap.db.AppDatabase;
import four.com.placesrecommendationsonheremap.db.entities.Author;
import four.com.placesrecommendationsonheremap.db.entities.Message;
import four.com.placesrecommendationsonheremap.db.entities.ServerPlace;

public class StartActivity extends AppCompatActivity implements ServerConnectAsyncTask.ServerCallback {
    private final static String TAG = StartActivity.class.getName();

    private final int DELAY = 1000 * 5;

    final String[] simpleAnswers = {
            "Понял Вас!", "Уже ищу!", "Начинаю подбирать места..."
    };

    final String[] questions = {
            "Может ли место быть шумным?",  // без разницы, шумно, средне, тихо
            "Как вы относитесь к большому количеству народа?", // терпимо, не люблю, нравится
            "Как далеко Вы готовы ехать?"   // далеко, средне, в шаговой доступности
    };

    final String[][] answersNoisy = {
            {"нет", "плох"},
            {"средне"},
            {"обяз", "да", "конеч", "хорош"}

    };

    final String[][] answersLoc = {
            {"близ", "пеш"},
            {"средне"},
            {"разн", "далеко"}
    };

    final String[][] answersAnn = {
            {"хор", "нрав", "полож", "отл"},
            {"терп"},
            {"плох", "негат"},
    };

    private int currentQuestion = 0;
    private boolean isQuestionAsked = false;

    Button startButton;

    AppDatabase database;

    private Map map = null;
    private SupportMapFragment mapFragment = null;

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private PositioningManager.OnPositionChangedListener positionListener;

    private Author chatBot;
    private Author client;

    private MessageInput messageInput;

    private MessagesListAdapter<Message> adapter;

    private ObjectMapper mapper = new ObjectMapper();

    private Timer timer;

    private BottomSheetBehavior bottomSheetBehavior;

    private LinearLayout llBottomSheet;

    private List<MapObject> mapObjects = new ArrayList<>();

    private List<ServerPlace> nearestPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();

        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "PreceqDB").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        startButton = findViewById(R.id.start_button);
        llBottomSheet = findViewById(R.id.bottomSheetLL);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
////                    hideKeyboard(getParent());
//                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                openInput(v);
            }
        });

        client = new Author();
        client.setId("2");
        client.setName("client");

        chatBot = new Author();
        chatBot.setId("1");
        chatBot.setName("ChatBot");

        MessagesList messagesList = findViewById(R.id.messagesList);

        adapter = new MessagesListAdapter<>(client.getId(), new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {

            }
        });
        messagesList.setAdapter(adapter);

        Message greating = new Message();
        greating.setId("0");
        greating.setText("Привет, чем займемся?;)");
        greating.setAuthor(chatBot);
        adapter.addToStart(greating, true);

        messageInput = findViewById(R.id.input);
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                if (timer != null) {
                    timer.cancel();
                }
                Message message = new Message();
                message.setAuthor(client);
                message.setText(input.toString());
                message.setId((input.hashCode() % 100) + "");
                adapter.addToStart(message, true);
                Message message1 = new Message();
                message1.setAuthor(chatBot);
                message1.setId(new Random().nextInt(100)+"");
                message1.setText(simpleAnswers[new Random().nextInt(3)]);
                adapter.addToStart(message1, true);

                sendRequest(input.toString());

                if (isQuestionAsked) {
                    isQuestionAsked = false;
                }

                if (currentQuestion < questions.length) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.setAuthor(chatBot);
                                    message.setText(questions[currentQuestion]);
                                    message.setId(questions[currentQuestion]);
                                    adapter.addToStart(message, true);

                                    currentQuestion++;
                                    isQuestionAsked = true;
                                }
                            });
                        }
                    }, DELAY);
                }
                return true;
            }
        });

    }

    private void openInput(final View v) {
        v.animate()
                .translationY(250)
                .alpha(0.0f).setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        v.setVisibility(View.GONE);
                    }
                });
        showMap();
    }

    private void showMap() {
        mapFragment.getView().setVisibility(View.VISIBLE);
        mapFragment.getView().setAlpha(0.0f);
        mapFragment.getView().animate().setDuration(500).alpha(1.0f);
        findViewById(R.id.bottomSheetLL).setVisibility(View.VISIBLE);
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initialize() {
        setContentView(R.layout.activity_start);
        // Search for the map fragment to finish setup by calling init().
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);

        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    map.getPositionIndicator().setVisible(true);
                    mapFragment.getView().setVisibility(View.GONE);

                    positionListener = new
                            PositioningManager.OnPositionChangedListener() {

                                public void onPositionUpdated(PositioningManager.LocationMethod method,
                                                              GeoPosition position, boolean isMapMatched) {
                                }

                                public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                                                 PositioningManager.LocationStatus status) {
                                }
                            };
                    PositioningManager.getInstance().addListener(
                            new WeakReference<>(positionListener));
                    PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS);
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });

    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    private void sendRequest(String clientInput) {
        String request;
        if (isQuestionAsked) {
            if (currentQuestion - 1 == 0) {
                request = "{\"type\": \"noisy\", \"value\": " + decideInput(clientInput, 0) + "}";
            } else if (currentQuestion - 1 == 1) {
                request = "{\"type\": \"occupancy\", \"value\":" + decideInput(clientInput, 2) + "}";
            } else {
                positionOnMap(nearestPlaces);
                showPlaces(nearestPlaces);
                return;
            }
        } else {
            request = "{ \"s\": \"" + clientInput + "\"}";
        }
        (new ServerConnectAsyncTask(this, request)).execute();
    }

    private int decideInput(String input, int type) {
        String[][] answers;
        if (type == 0) {
            answers = answersNoisy;
        } else if (type == 1) {
            answers = answersLoc;
        } else {
            answers = answersAnn;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < answers[i].length; j++) {
                if (input.toLowerCase().contains(answers[i][j])) {
                    return i;
                }
            }
        }

        return 2;
    }

    @Override
    public void onResult(String string) {
        if (string == null) {
            return;
        }

        try {
            Log.d(TAG, string);
            List<ServerPlace> places = mapper.readValue(string,
                    mapper.getTypeFactory().constructCollectionType(List.class, ServerPlace.class));

            StringBuilder builder = new StringBuilder("Не хочешь сходить?\n\n");

            int count = 0;
            List<ServerPlace> placesToShow;
            if (places.size() > 10) {
                placesToShow = places.subList(0, 9);
            } else {
                placesToShow = places;
            }
            for (ServerPlace serverPlace : placesToShow) {
                count++;
                builder.append(count).append(") ").append(serverPlace.getTitle()).append("\n");
                builder.append("  ").append(serverPlace.getCategories()).append("\n");
                builder.append("  ").append(serverPlace.getAddress()).append("\n");
                builder.append("  ").append(serverPlace.getTimetable()).append("\n");
            }

            String messageText = builder.toString();

            Message message = new Message();
            message.setId(messageText.hashCode() % 100 + "");
            message.setText(messageText);
            message.setAuthor(chatBot);
            adapter.addToStart(message, true);

            positionOnMap(places);

            nearestPlaces = findNearest(places);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error while reading");
        }
    }

    private List<ServerPlace> findNearest(List<ServerPlace> places) {
        List<Pair<Double, Integer>> distances = new ArrayList<>();
        int counter = 0;
        for (ServerPlace place : places) {
            distances.add(new Pair<Double, Integer>(distance(place.getCoords().getLat(),
                    59.999653, place.getCoords().getLon(), 30.310220), counter));
            counter++;
        }

        distances.sort(new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
                if (o1.first < o2.first) {
                    return -1;
                } else if (o1.first > o2.first) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        List<ServerPlace> nearestPlaces = new ArrayList<>();

        int countOfNearest = 10;
        if (distances.size() < countOfNearest) {
            countOfNearest = distances.size();
        }

        for (int i = 0; i < countOfNearest; i++) {
            nearestPlaces.add(places.get(distances.get(i).second));
        }

        return nearestPlaces;
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    private void positionOnMap(List<ServerPlace> places) {
        if (!mapObjects.isEmpty()) {
            map.removeMapObjects(mapObjects);
            mapObjects.clear();
        }

        for (ServerPlace place : places) {
            MapMarker marker = new MapMarker(new GeoCoordinate(place.getCoords().getLat(), place.getCoords().getLon()));
            mapObjects.add(marker);
            map.addMapObject(marker);
        }
    }

    private void showPlaces(List<ServerPlace> placesToShow) {
        int count = 0;
        StringBuilder builder = new StringBuilder("Не хочешь сходить?\n\n");

        for (ServerPlace serverPlace : placesToShow) {
            count++;
            builder.append(count).append(") ").append(serverPlace.getTitle()).append("\n");
            builder.append("  ").append(serverPlace.getCategories()).append("\n");
            builder.append("  ").append(serverPlace.getAddress()).append("\n");
            builder.append("  ").append(serverPlace.getTimetable()).append("\n");
        }

        String messageText = builder.toString();

        Message message = new Message();
        message.setId(messageText.hashCode() % 100 + "");
        message.setText(messageText);
        message.setAuthor(chatBot);
        adapter.addToStart(message, true);
    }

}
