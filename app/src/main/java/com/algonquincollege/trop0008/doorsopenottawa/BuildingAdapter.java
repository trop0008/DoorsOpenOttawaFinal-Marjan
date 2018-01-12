package com.algonquincollege.trop0008.doorsopenottawa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.trop0008.doorsopenottawa.model.BuildingPOJO;
import com.algonquincollege.trop0008.doorsopenottawa.services.MyService;
import com.algonquincollege.trop0008.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.trop0008.doorsopenottawa.utils.RequestPackage;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;
import static com.algonquincollege.trop0008.doorsopenottawa.MainActivity.JSON_URL;

/**
 * Open Doors Ottawa App
 *
 * @author Marjan Tropper (trop0008@algonquinlive.com)
 */
/**
 * BuildingAdapter.
 *
 */
public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    public static final String BUILDING_KEY = "building_key";

    private Context            mContext;
    private List<BuildingPOJO> mBuildings;

    public BuildingAdapter(Context context, List<BuildingPOJO> buildings) {
        this.mContext = context;
        this.mBuildings = buildings;
    }

    @Override
    public BuildingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View buildingView = inflater.inflate(R.layout.list_building, parent, false);
        ViewHolder viewHolder = new ViewHolder(buildingView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BuildingAdapter.ViewHolder holder, final int position) {
        final BuildingPOJO aBuilding = mBuildings.get(position);

        holder.tvName.setText(aBuilding.getNameEN());
        holder.tvAddress.setText(aBuilding.getAddressEN());

        //FIXME :: LOCALHOST
        String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + aBuilding.getBuildingId() + "/image";
        //url = "http://10.0.2.2:3000/buildings/" + aBuilding.getBuildingId() + "/image";
        Picasso.with(mContext)
                .load(Uri.parse(url))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimagefound)
                .resize(96, 96)
                .into(holder.imageView);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailBuildingActivity.class);
                intent.putExtra(BUILDING_KEY, aBuilding);
                mContext.startActivity(intent);
            }
        });


        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if ( (holder.bDeleteBuilding.getVisibility() == View.VISIBLE)
                        && (holder.bEditBuilding.getVisibility() == View.VISIBLE)) {
                    holder.bDeleteBuilding.setVisibility(View.INVISIBLE);
                    holder.bDeleteBuilding.setVisibility(View.INVISIBLE);
                } else {
                    holder.bDeleteBuilding.setVisibility(View.VISIBLE);
                    holder.bEditBuilding.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        holder.bDeleteBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                // TODO - externalize strings to strings.xml
                builder.setTitle("Confirm")
                        .setMessage("Delete " + aBuilding.getBuildingId() + " - " + aBuilding.getNameEN() + "?")

                        // Displays: OK
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete this course
                                Toast.makeText(mContext, "Deleted Building: " + aBuilding.getBuildingId(), Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Deleted Building: " + aBuilding.getBuildingId());
                                holder.bDeleteBuilding.setVisibility(View.INVISIBLE);
                                holder.bEditBuilding.setVisibility(View.INVISIBLE);

                                // TODO - for Doors Open app, replace these two lines:
                                //mCourses.remove(position);
                                //CourseAdapter.this.notifyDataSetChanged();
                                // With call to intent service to DELETE /buildings/:id
                                //
                                 RequestPackage requestPackage = new RequestPackage();
                                 requestPackage.setMethod( HttpMethod.DELETE );

                                 requestPackage.setEndPoint( JSON_URL +"/"+ aBuilding.getBuildingId() );

                                 Intent intent = new Intent(mContext , MyService.class);

                                 intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
                                 mContext.startService(intent);

                                dialog.dismiss();
                            }
                        })

                        // Displays: Cancel
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();

                                Toast.makeText(mContext, "CANCELLED: Deleted Buildining: " + aBuilding.getBuildingId(), Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "CANCELLED: Deleted Course: " + aBuilding.getBuildingId());
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mBuildings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public ImageView imageView;
        public View mView;
        public  TextView tvAddress;
        public ImageButton bEditBuilding;
        public ImageButton bDeleteBuilding;
        public ViewHolder(View buildingView) {
            super(buildingView);

            tvName = (TextView) buildingView.findViewById(R.id.buildingNameText);
            imageView = (ImageView) buildingView.findViewById(R.id.imageView);
            tvAddress = (TextView) buildingView.findViewById(R.id.buildingAddressText);
            mView = buildingView;
            bEditBuilding = (ImageButton) buildingView.findViewById(R.id.editBuildingButton);
            bDeleteBuilding = (ImageButton) buildingView.findViewById(R.id.deleteBuildingButton);
        }
    }
}
