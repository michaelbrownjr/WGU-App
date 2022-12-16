package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mbro.wguapp.R;
import entities.TermEntity;

import java.util.ArrayList;
import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermHolder> {
    private List<TermEntity> terms = new ArrayList<>();
    private OnItemClickListener listener;

    // method for filtering our recyclerview items.
    public void filterList(List<TermEntity> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        terms = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TermHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.term_item, parent, false);
        return new TermHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TermHolder holder, int position) {
        TermEntity currentTerm = terms.get(position);
        holder.textViewTermTitle.setText(currentTerm.getTitle());
        holder.textViewTermStartDate.setText(currentTerm.getStart());
        holder.getTextViewTermEndDate.setText(currentTerm.getEnd());
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public void setTerms(List<TermEntity> terms) {
        this.terms = terms;
        notifyDataSetChanged();
    }

    public TermEntity getTermAt(int postition) {
        return terms.get(postition);
    }

    class TermHolder extends RecyclerView.ViewHolder {
        private TextView textViewTermTitle;
        private TextView textViewTermStartDate;
        private TextView getTextViewTermEndDate;

        public TermHolder(@NonNull View itemView) {
            super(itemView);
            textViewTermTitle = itemView.findViewById(R.id.text_view_term_title);
            textViewTermStartDate = itemView.findViewById(R.id.text_view_term_start_date);
            getTextViewTermEndDate = itemView.findViewById(R.id.text_view_term_end_date);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(terms.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TermEntity termEntity);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
