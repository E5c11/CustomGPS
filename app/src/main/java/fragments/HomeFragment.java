package fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.esc.test.customgps.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment{

    private View view;
    private CircleImageView startBtn;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int Request_User_Location_Code = 99;
    private static final String TAG = "myT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        super.onCreate(savedInstanceState);

        checkUserLocationPermission();

        setButtonListener();

        return view;
    }

    private boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            return false;
        } else {
            return true;
        }
    }

    private void setButtonListener() {
        startBtn = view.findViewById(R.id.start_workout_btn);
        startBtn.setOnClickListener(v -> {
            if (checkUserLocationPermission()) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToWorkoutFragment();
                Navigation.findNavController(v).navigate(action);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkUserLocationPermission();
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
