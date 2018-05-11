package tanvir.crimelogger_ewu.HelperClass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tanvir.crimelogger_ewu.R;

/**
 * Created by USER on 01-Feb-17.
 */

public class RecyclerAdapterToShowPolicePhoneNumber extends RecyclerView.Adapter<RecyclerAdapterToShowPolicePhoneNumber.RecyclerViewHolder> {


    ArrayList<String> policeNumAL;
    Context context;
    String thanaPhoneNumber="";
    String thanaName="";

    public RecyclerAdapterToShowPolicePhoneNumber(Context context, ArrayList<String> policeNumAL) {
        this.context = context;
        this.policeNumAL=policeNumAL;


    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_to_inflate_in_police_number, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, policeNumAL);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {


        separateThanaNameAndThanaPhoneNumber(position);
        holder.thanaName.setText(thanaName);
        holder.thaneNumber.setText(thanaPhoneNumber);


       /// holder.ocNumber.setText(policeNumAL.get(position));


    }

    @Override
    public int getItemCount() {
        return policeNumAL.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder

    {

        Context context;

        TextView thanaName ;
        TextView thaneNumber;
        ImageView imageView;
        LinearLayout linearLayout;


        public RecyclerViewHolder(View view, final Context context, final ArrayList<String> policeNumAL) {
            super(view);

            thanaName = view.findViewById(R.id.thanaName);
            thaneNumber=view.findViewById(R.id.thanaPhoneNumber);
            imageView=view.findViewById(R.id.phoneIC);
            linearLayout=view.findViewById(R.id.policeLinearLayout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    separateThanaNameAndThanaPhoneNumber(position);

                    Uri call = Uri.parse("tel:" + thanaPhoneNumber);
                    Intent surf = new Intent(Intent.ACTION_DIAL, call);
                    context.startActivity(surf);
                }
            });



        }
    }

    public void separateThanaNameAndThanaPhoneNumber(int position)
    {
        String thanaDescription = policeNumAL.get(position);

        int seperatePosition = thanaDescription.indexOf('/');

        thanaName = thanaDescription.substring(0,seperatePosition);
        thanaName=thanaName.trim();

        thanaPhoneNumber = thanaDescription.substring(seperatePosition+1);
        thanaPhoneNumber=thanaPhoneNumber.trim();
    }
}