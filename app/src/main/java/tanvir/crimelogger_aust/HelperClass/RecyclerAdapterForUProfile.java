package tanvir.crimelogger_aust.HelperClass;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tanvir.crimelogger_aust.MOdelClass.UserPostMC;
import tanvir.crimelogger_aust.R;

/**
 * Created by HP on 26-Nov-17.
 */

public class RecyclerAdapterForUProfile extends RecyclerView.Adapter<RecyclerAdapterForUProfile.RecyclerViewHolder> {
    ArrayList<UserPostMC> userPostMCS;
    Context context;

    public RecyclerAdapterForUProfile(Context context, ArrayList<UserPostMC> userPostMCS) {
        this.context = context;
        this.userPostMCS = userPostMCS;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layou_to_inflate_in_recyclerview, parent, false);

        RecyclerAdapterForUProfile.RecyclerViewHolder recyclerViewHolder = new RecyclerAdapterForUProfile.RecyclerViewHolder(view, context, userPostMCS);
        ;
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterForUProfile.RecyclerViewHolder holder, int position) {

        holder.crimePlace.setText(userPostMCS.get(position).getCrimePlace());
        holder.crimeDate.setText(userPostMCS.get(position).getCrimeDate());
        holder.crimeTime.setText(userPostMCS.get(position).getCrimeTime());
        holder.crimeType.setText(userPostMCS.get(position).getCrimeType());
        holder.crimeDesc.setText(userPostMCS.get(position).getCrimeDesc());
        holder.userName.setText(userPostMCS.get(position).getUserName());
        holder.postDateAndTime.setText(userPostMCS.get(position).getPostDateAndTime());


    }

    @Override
    public int getItemCount() {
        return userPostMCS.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder

    {
        TextView userName, crimePlace, crimeDate, crimeTime, crimeType, crimeDesc, postDateAndTime;

        ArrayList<UserPostMC> userPostMCS;

        LinearLayout linearLayout;


        Context context;


        public RecyclerViewHolder(View view, final Context context, final ArrayList<UserPostMC> userPostMCS) {
            super(view);

            this.context = context;
            this.userPostMCS = userPostMCS;

            userName = view.findViewById(R.id.postedByTV);
            crimePlace = view.findViewById(R.id.crimePlaceTV);
            crimeDate = view.findViewById(R.id.crimeDateTV);
            crimeTime = view.findViewById(R.id.crimeTimeTV);
            crimeType = view.findViewById(R.id.crimeTypeTV);
            crimeDesc = view.findViewById(R.id.crimeDescTV);
            postDateAndTime = view.findViewById(R.id.postDateTV);

            linearLayout = view.findViewById(R.id.recylerViewLL);

            linearLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    Toast.makeText(context, Integer.toString(position), Toast.LENGTH_SHORT).show();


                }
            });


        }


    }
}
