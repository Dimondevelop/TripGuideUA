package ua.tripguide.tripguideua;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Locale;
import java.util.Objects;

import ua.tripguide.tripguideua.Utils.UniversalImageLoader;

public class MoreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String objectName;
    private float coordinate_x;
    private float coordinate_y;
    String thumbnail_object;
    String object_description;
    String working_hours;
    String type_object;
    ImageLoader imageLoader = ImageLoader.getInstance();

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getBaseContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initImageLoader();
        super.onCreate(savedInstanceState);
        String languageToLoad = "uk_UA";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_more);


        Intent intent = getIntent();
        objectName = Objects.requireNonNull(intent.getExtras()).getString("name_object");
        coordinate_x = Objects.requireNonNull(intent.getExtras()).getFloat("coordinate_x");
        coordinate_y = Objects.requireNonNull(intent.getExtras()).getFloat("coordinate_y");
        thumbnail_object = Objects.requireNonNull(intent.getExtras()).getString("thumbnail_object");
        object_description = Objects.requireNonNull(intent.getExtras()).getString("object_description");
        working_hours = Objects.requireNonNull(intent.getExtras()).getString("working_hours");
        type_object = Objects.requireNonNull(intent.getExtras()).getString("type_object");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(objectName);

        ImageView thumbnailObject = findViewById(R.id.iv_thumbnail_object);
        TextView justifiedParagraph = findViewById(R.id.tv_description_object);
        TextView tvWorkingHours = findViewById(R.id.tv_working_hours);
        final TextView tvErrorImage = findViewById(R.id.tv_error_image);
        TextView tv_map_name = findViewById(R.id.tv_map_name);


        imageLoader.displayImage(thumbnail_object, thumbnailObject, null, new  ImageLoadingListener () {

            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (failReason.getType() == FailReason.FailType.IO_ERROR) {
                    tvErrorImage.setText("Відсутнє інтернет з'єднання.");
                    tvErrorImage.setVisibility(View.VISIBLE);
                } else {
                    tvErrorImage.setText("Сталася помилка.");
                    tvErrorImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                imageLoader.displayImage(thumbnail_object,(ImageView)view);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        justifiedParagraph.setText(object_description);
        tvWorkingHours.setText(String.format("%s %s\n\n%s\n%s", getString(R.string.type_of), type_object,getString(R.string.working_hours), working_hours));
        tv_map_name.setText(String.format("%s %s", objectName, getString(R.string.on_map)));


        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(coordinate_x, coordinate_y);
        mMap.addMarker(new MarkerOptions().position(latLng)
                .title(objectName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 50, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
