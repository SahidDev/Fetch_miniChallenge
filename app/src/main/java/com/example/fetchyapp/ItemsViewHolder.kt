import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchyapp.ItemCollection
import com.example.fetchyapp.R

class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val idTextView: TextView = itemView.findViewById(R.id.IdTextView)
    val itemsList: LinearLayout = itemView.findViewById(R.id.ItemsLinearView)
}
