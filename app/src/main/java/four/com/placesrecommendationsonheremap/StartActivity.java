package four.com.placesrecommendationsonheremap;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import four.com.placesrecommendationsonheremap.db.AppDatabase;
import four.com.placesrecommendationsonheremap.db.entities.Author;
import four.com.placesrecommendationsonheremap.db.entities.Message;

public class StartActivity extends AppCompatActivity implements ServerConnectAsyncTask.ServerCallback {

    Button startButton;
    Button findButton;
    EditText kekButton;

    AppDatabase database;

    private Map map = null;
    private SupportMapFragment mapFragment = null;

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private boolean paused = false;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private PositioningManager.OnPositionChangedListener positionListener;

    private Author chatBot;
    private Author client;

    private MessageInput messageInput;

    private MessagesListAdapter<Message> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();

        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "PreceqDB").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        startButton = (Button) findViewById(R.id.start_button);
        kekButton = (EditText) findViewById(R.id.kek_button);
        findButton = (Button) findViewById(R.id.find_button);

        kekButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!kekButton.getText().toString().equals("")
                        && findButton.getVisibility() != View.VISIBLE) {
                    findButton.setVisibility(View.VISIBLE);
                    findButton.setAlpha(0.0f);
                    findButton.animate().alpha(1.0f).setDuration(500);
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                openInput(v);
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(StartActivity.this);

                if (mapFragment.getView().getVisibility() != View.VISIBLE) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int height = size.y;

                    moveDown(kekButton, height / 2.0f - kekButton.getHeight() - findButton.getHeight());
                    moveDown(findButton, height / 2.0f - kekButton.getHeight() - findButton.getHeight());
                    showMap();
                    kekButton.clearFocus();
                }
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
                Message message = new Message();
                message.setAuthor(client);
                message.setText(input.toString());
                message.setId((input.hashCode() % 100) + "");
                adapter.addToStart(message, true);
                sendRequest(input.toString());
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

                        kekButton.setVisibility(View.VISIBLE);
                        kekButton.setAlpha(0.0f);
                        kekButton.animate()
                                .translationY(0)
                                .alpha(1.0f)
                                .setDuration(500);
                    }
                });
    }

    private void showMap() {
        mapFragment.getView().setVisibility(View.VISIBLE);
        mapFragment.getView().setAlpha(0.0f);
        mapFragment.getView().animate().setDuration(500).alpha(1.0f);
        findViewById(R.id.bottomSheetLL).setVisibility(View.VISIBLE);
    }

    private void moveDown(final View v, float distance) {
        v.animate().translationY(distance).setDuration(500);
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
                                    if (!paused) {
                                        map.setCenter(position.getCoordinate(),
                                                Map.Animation.NONE);
                                    }
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
        (new ServerConnectAsyncTask(this, clientInput)).execute();
    }

    @Override
    public void onResult(String string) {
        Message message = new Message();
        message.setId(string.hashCode() % 100 + "");
        message.setText(string);
        message.setAuthor(chatBot);
        adapter.addToStart(message, true);
    }
}
