package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mbro.wguapp.R;
import activities.CourseActivity;
import entities.CourseEntity;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseHolder> {
    private List<CourseEntity> courses = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_item, parent, false);
        return new CourseHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int position) {
        CourseEntity currentCourse = courses.get(position);
        holder.textViewCourseTitle.setText(currentCourse.getTitle());
        holder.textViewCourseEndDate.setText(currentCourse.getEndDate());
        holder.textViewStatus.setText(CourseActivity.getStatus(currentCourse.getStatus()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void setCourses(List<CourseEntity> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    public CourseEntity getCourseAt(int position) {
        return courses.get(position);
    }

    class CourseHolder extends RecyclerView.ViewHolder {
        private TextView textViewCourseTitle;
        private TextView textViewCourseEndDate;
        private TextView textViewStatus;

        public CourseHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourseTitle = itemView.findViewById(R.id.text_view_course_title);
            textViewCourseEndDate = itemView.findViewById(R.id.text_view_course_end_date);
            textViewStatus = itemView.findViewById(R.id.text_view_course_status);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(courses.get(position));
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(CourseEntity courseEntity);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
