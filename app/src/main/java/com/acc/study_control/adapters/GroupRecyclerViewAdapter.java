package com.acc.study_control.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acc.study_control.R;
import com.acc.study_control.activities.MainActivity;
import com.acc.study_control.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by acondori on 19/05/2018.
 */

public class GroupRecyclerViewAdapter extends
        RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private List<Group> eventsList;
    private Context context;
    private FirebaseFirestore firestoreDB;

    public GroupRecyclerViewAdapter(List<Group> list, Context ctx, FirebaseFirestore firestore) {
        eventsList = list;
        context = ctx;
        firestoreDB = firestore;
    }
    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    @Override
    public GroupRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_group, parent, false);

        GroupRecyclerViewAdapter.ViewHolder viewHolder =
                new GroupRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GroupRecyclerViewAdapter.ViewHolder holder, int position) {
        final int itemPos = position;
        final Group event = eventsList.get(position);
        holder.name.setText(event.name);
        holder.place.setText(event.name);
        holder.startTime.setText("" + event.name);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEventFragment(event);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent(event.id, itemPos);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView place;
        public TextView startTime;

        public Button edit;
        public Button delete;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.name_tv);
            place = (TextView) view.findViewById(R.id.place_tv);
            startTime = (TextView) view.findViewById(R.id.start_time_tv);

            edit = view.findViewById(R.id.edit_event_b);
            delete = view.findViewById(R.id.delete_event_b);
        }
    }

    private void editEventFragment(Group event){
        FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();

        Bundle bundle=new Bundle();
        //bundle.putParcelable("event", event);

        //AddEventFragment addFragment = new AddEventFragment();
        //addFragment.setArguments(bundle);

        //fm.beginTransaction().replace(R.id.events_content, addFragment).commit();
    }
    private void deleteEvent(String docId, final int position){
        firestoreDB.collection("events").document(docId).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        eventsList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, eventsList.size());
                        Toast.makeText(context,
                                "Event document has been deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}