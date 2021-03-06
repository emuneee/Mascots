package com.emuneee.mascots;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.emuneee.mascots.model.BlueDevil;
import com.emuneee.mascots.model.Eagle;
import com.emuneee.mascots.model.Mascot;
import com.emuneee.mascots.model.MascotUtil;
import com.emuneee.mascots.model.MrWuf;
import com.emuneee.mascots.model.Rameses;

import junit.framework.Test;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    static final List<Mascot> MASCOTS = Arrays.asList(new MrWuf(), new Rameses(),
            new Eagle(), new BlueDevil());
    RecyclerView mascotsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mascotsRecycler = (RecyclerView) findViewById(R.id.mascots);
        MascotsAdapter adapter = new MascotsAdapter(this,
                MASCOTS.stream()
                .filter(Mascot::isPointing)
                .collect(Collectors.toList()));
        mascotsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mascotsRecycler.setAdapter(adapter);
        getSupportActionBar().setTitle(String.format("%d Mascots", adapter.getItemCount()));
    }

    private static class MascotsAdapter extends RecyclerView.Adapter<MascotHolder> {

        private WeakReference<Context> contextRef;
        private final List<Mascot> mascotList;

        private MascotsAdapter(Context context, List<Mascot> mascotList) {
            this.mascotList = mascotList;
            contextRef = new WeakReference<>(context);
        }

        @Override
        public MascotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MascotHolder(LayoutInflater.from(contextRef.get()).inflate(R.layout.view_mascot, parent, false));
        }

        @Override
        public void onBindViewHolder(MascotHolder holder, int position) {
            Mascot mascot = mascotList.get(position);
            holder.name.setText(mascot.getName());
            holder.college.setText(MascotUtil.getFullCollegeName(mascot));
            holder.phrase = mascot.getPhrase();
            holder.whoAmI = String.format("I AM %s", mascot.getName().toUpperCase());

            SeenAtPlace[] seenAtPlaces = mascot.getClass().getAnnotationsByType(SeenAtPlace.class);
            StringBuilder seenAtStr = new StringBuilder();

            for (int i = 0; i < seenAtPlaces.length; i++) {
                seenAtStr.append(seenAtPlaces[i].name()).append(" ");
            }
            holder.seenAt.setText(seenAtStr.toString());
            Glide.with(contextRef.get())
                    .fromUri()
                    .load(Uri.parse(mascot.getMascotImageUrl()))
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return mascotList.size();
        }
    }

    private static class MascotHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView college;
        TextView seenAt;
        ImageView image;
        String phrase;
        String whoAmI;
        TestLambda testLambda;

        public MascotHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            college = (TextView) itemView.findViewById(R.id.college);
            image = (ImageView) itemView.findViewById(R.id.image);
            seenAt = (TextView) itemView.findViewById(R.id.seen_at);

            itemView.setOnClickListener(this::showToast);

            itemView.setOnLongClickListener(view -> {

                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        // PRETEND THIS IS A BLOCKING HTTP API CALL
                        // :-)
                        return whoAmI;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        Toast.makeText(view.getContext(), s, Toast.LENGTH_SHORT).show();
                    }
                }.execute();

                return true;
            });

            testLambda = (i, j) -> {

            };
        }

        private void showToast(View view) {
            Toast.makeText(view.getContext(), phrase, Toast.LENGTH_SHORT).show();
        }

        private interface TestLambda {
            void helloWorld(int i, int j);
        }
    }
}
