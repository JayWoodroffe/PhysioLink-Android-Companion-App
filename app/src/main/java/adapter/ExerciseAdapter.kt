package adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import hu.bme.aut.fna1a3.physiolink.R
import model.ExerciseModel

class ExerciseAdapter(
    private val context: Context,
    private val exercises: List<ExerciseModel>,
    private val listener: ExerciseAdapterListener
) : BaseAdapter() {

    private class ExerciseViewHolder {
        lateinit var tvTitle: TextView
        lateinit var tvDescription: TextView
        lateinit var tvSetsReps: TextView
    }

    override fun getCount(): Int {
        return exercises.size
    }

    override fun getItem(position: Int): Any {
        return exercises[position]
    }

    override fun getItemId(position: Int): Long {
        return exercises[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView = convertView
        val holder: ExerciseViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.exercise_card, parent, false)
            holder = ExerciseViewHolder()
            holder.tvTitle = itemView.findViewById(R.id.tvTitle)
            holder.tvDescription = itemView.findViewById(R.id.tvDescription)
            holder.tvSetsReps = itemView.findViewById(R.id.tvSetsReps)
            itemView.tag = holder
        } else {
            holder = itemView.tag as ExerciseViewHolder
        }

        val exercise = exercises[position]

        holder.tvTitle.text = exercise.name
        holder.tvDescription.text = exercise.description.take(40) + if (exercise.description.length > 40) "..." else ""
        holder.tvSetsReps.text = "${exercise.sets} sets x ${exercise.reps} reps"

        itemView?.setOnClickListener {
            listener.onItemClick(position)
        }

        return itemView!!
    }
}