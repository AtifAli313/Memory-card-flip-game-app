package com.test3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {

    private final List<MemoryCard> cards;
    private final OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    public MemoryAdapter(List<MemoryCard> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View cardBack;
        private final View cardFront;
        private final View cardMatched;
        private final ImageView cardImage;
        private final ImageView matchedImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBack = itemView.findViewById(R.id.cardBack);
            cardFront = itemView.findViewById(R.id.cardFront);
            cardMatched = itemView.findViewById(R.id.cardMatched);
            cardImage = itemView.findViewById(R.id.cardImage);
            matchedImage = itemView.findViewById(R.id.matchedImage);
        }

        public void bind(int position) {
            MemoryCard card = cards.get(position);
            
            // Reset visibility
            cardBack.setVisibility(View.GONE);
            cardFront.setVisibility(View.GONE);
            cardMatched.setVisibility(View.GONE);

            if (card.isMatched()) {
                cardMatched.setVisibility(View.VISIBLE);
                matchedImage.setImageResource(card.getIdentifier());
            } else if (card.isFlipped()) {
                cardFront.setVisibility(View.VISIBLE);
                cardImage.setImageResource(card.getIdentifier());
            } else {
                cardBack.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> listener.onCardClick(position));
        }
    }
}
