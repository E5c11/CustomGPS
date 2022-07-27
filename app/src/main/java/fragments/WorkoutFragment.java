package fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.esc.test.customgps.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.ParseException;

import services.ActivityService;
import viewmodels.LocationViewModel;


public class WorkoutFragment extends Fragment implements OnMapReadyCallback {

    private LocationViewModel locationViewModel;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Button button, stop;
    private TextView distance, avgSpeed, alt, speed, time;
    private ProgressBar progressBar;
    private Bundle mapViewBundle;
    private MapView mapView;
    private View view;
    private static final String TAG = "myT";
    private NavController navController;

    private final BroadcastReceiver pauseBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: ");
            if (action != null) {
                if (action.equals(getResources().getString(R.string.pause))) {
                    button.setText(getResources().getString(R.string.resume));
                    stop.setVisibility(View.VISIBLE);
                } else {
                    button.setText(getResources().getString(R.string.pause));
                    stop.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.workout_fragment, container, false);
        super.onCreate(savedInstanceState);
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        Bundle bundle = getArguments();
        locationViewModel.reopenedWorkout(bundle.getString(getResources().getString(R.string.active_workout)));
        progressBar = view.findViewById(R.id.progress_bar);
        mapSetup(savedInstanceState);

        return view;
    }

    private void mapSetup(Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        setNavController();
    }

    private void setNavController() {
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_container);
        setHasOptionsMenu(true);
    private void mapSetup(Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        setNavController();
    }

    private void setNavController() {
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_container);
        setHasOptionsMenu(true);
        setListeners();
    }

    private void setListeners() {
        button = view.findViewById(R.id.button);
        stop = view.findViewById(R.id.stop);
        button.setOnClickListener(b -> startPauseWorkout());
        stop.setOnClickListener(s -> stopIntentService());
        registerBroadcast();
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getResources().getString(R.string.pause));
        intentFilter.addAction(getResources().getString(R.string.resume));
        getActivity().registerReceiver(pauseBroadcast, intentFilter);
        locationViewModel.getRunningState().observe(getViewLifecycleOwner(), s -> {
            button.setText(s);
            Intent intent = new Intent();
            intent.setAction(getResources().getString(R.string.restart_app));
            getActivity().sendBroadcast(intent);
        });
        locationViewModel.getSetObservers().observe(getViewLifecycleOwner(), s -> {if (s) setObservers();});
    }

    private void setObservers() {
        locationViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                locationViewModel.getWorkoutInfo(s);
                progressBar.setVisibility(View.GONE);
            }
        });
        distance = view.findViewById(R.id.distance_text);
        avgSpeed = view.findViewById(R.id.avg_speed_text);
        alt = view.findViewById(R.id.altitude_text);
        speed = view.findViewById(R.id.speed_text);
        time = view.findViewById(R.id.time_text);
        locationViewModel.getDistance().observe(getViewLifecycleOwner(), s -> distance.setText("Distance: " + s));
        locationViewModel.getAvgSpeed().observe(getViewLifecycleOwner(), s -> avgSpeed.setText("Avg speed: " + s));
        locationViewModel.getAlt().observe(getViewLifecycleOwner(), s -> alt.setText("Altitude: " + s));
        locationViewModel.getSpeed().observe(getViewLifecycleOwner(), s -> speed.setText("Speed: " + s));
        locationViewModel.getTime().observe(getViewLifecycleOwner(), s -> time.setText("Time: " + s));
    }


    @SuppressLint("MissingPermission")
    private void startPauseWorkout() {
        String serviceMsg;
        if (button.getText().equals(getResources().getString(R.string.start))) {
            progressBar.setVisibility(View.VISIBLE);
            button.setText(getResources().getString(R.string.pause));
            serviceMsg = getResources().getString(R.string.start_service);
            setObservers();
        } else if (button.getText().equals(getResources().getString(R.string.resume))) {
            locationViewModel.resumeWorkout();
            button.setText(getResources().getString(R.string.pause));
            serviceMsg = getResources().getString(R.string.resume_service);
            stop.setVisibility(View.GONE);
        } else {
            locationViewModel.pauseWorkout();
            button.setText(getResources().getString(R.string.resume));
            serviceMsg = getResources().getString(R.string.pause_service);
            stop.setVisibility(View.VISIBLE);
        }
        Intent newActivityIntent = new Intent(getActivity(), ActivityService.class);
        newActivityIntent.putExtra(getResources().getString(R.string.service_msg), serviceMsg);
        ContextCompat.startForegroundService(getActivity(), newActivityIntent);
    }

    private void stopIntentService() {
        Intent stopActivityIntent = new Intent(getActivity(), ActivityService.class);
        stopActivityIntent.putExtra(getResources().getString(R.string.service_msg), getResources().getString(R.string.stop_service));
        ContextCompat.startForegroundService(getActivity(), stopActivityIntent);
        locationViewModel.clearWorkout();
        button.setText(getResources().getString(R.string.start));
        stop.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        mapView.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) navController.navigate(R.id.settingsFragment);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(pauseBroadcast);
    }
}
