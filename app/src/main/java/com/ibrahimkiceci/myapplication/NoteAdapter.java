package com.ibrahimkiceci.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahimkiceci.myapplication.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    ArrayList<Note>noteArrayList;

    public NoteAdapter(ArrayList<Note>noteArrayList){
        this.noteArrayList =noteArrayList;


    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NoteHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int position) {

        holder.binding.recyclerViewTextView.setText(noteArrayList.get(position).name);

        // Tiklaninca ne olacagini burada yaziyoruz;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), MainActivity2.class);
                intent.putExtra("info", "old");
                intent.putExtra("noteId", noteArrayList.get(holder.getAdapterPosition()).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {

        return noteArrayList.size();



    }

    public class NoteHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public NoteHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
