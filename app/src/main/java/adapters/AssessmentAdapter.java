package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mbro.wguapp.R;
import entities.AssessmentEntity;

import java.util.ArrayList;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentHolder> {
   private List<AssessmentEntity> assessments = new ArrayList<>();
   private OnItemClickListener listener;

   @NonNull
   @Override
   public AssessmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.assessment_item, parent, false);
       return new AssessmentHolder(itemView);
   }

   @Override
   public void onBindViewHolder(@NonNull AssessmentHolder holder, int position) {
       AssessmentEntity currentAssessment = assessments.get(position);
       holder.textViewAssessmentTitle.setText(currentAssessment.getName());
       holder.textViewAssessmentGoalDate.setText(currentAssessment.getGoalDate());
   }

   @Override
   public int getItemCount() {
      return assessments.size();
   }

   public void setAssessments(List<AssessmentEntity> assessments) {
      this.assessments = assessments;
      notifyDataSetChanged();
   }

   public AssessmentEntity getAssessmentAt(int position) {
      return assessments.get(position);
   }

   class AssessmentHolder extends RecyclerView.ViewHolder {
      private TextView textViewAssessmentTitle;
      private TextView textViewAssessmentGoalDate;

      public AssessmentHolder(@NonNull View itemView) {
         super(itemView);
         textViewAssessmentTitle = itemView.findViewById(R.id.text_view_assessment_title);
         textViewAssessmentGoalDate = itemView.findViewById(R.id.text_view_assessment_goal_date);

         itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if(listener != null && position != RecyclerView.NO_POSITION) {
               listener.onItemClick(assessments.get(position));
            }
         });
      }
   }

   public interface OnItemClickListener {
      void onItemClick(AssessmentEntity assessmentEntity);
   }

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.listener = listener;
   }

}
