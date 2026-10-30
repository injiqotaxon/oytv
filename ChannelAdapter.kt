
package com.oysha.tv
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oysha.tv.databinding.ItemChannelBinding
class ChannelAdapter(private val list: List<MainActivity.Channel>, private val onClick: (MainActivity.Channel) -> Unit) : RecyclerView.Adapter<ChannelAdapter.VH>() {
    class VH(val b: ItemChannelBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val ch = list[position]
        holder.b.txtName.text = ch.name
        holder.b.txtGroup.text = ch.group
        holder.b.root.setOnClickListener { onClick(ch) }
    }
    override fun getItemCount() = list.size
}
