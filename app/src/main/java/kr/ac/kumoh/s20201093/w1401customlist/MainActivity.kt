package kr.ac.kumoh.s20201093.w1401customlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20201093.w1401customlist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: SongViewModel
    private val songAdapter = SongAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[SongViewModel::class.java]

        binding.list.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = songAdapter
        }

        model.list.observe(this) {
            // 좀더 구체적인 이벤트를 사용하라고 warning 나와서 변경함
            //songAdapter.notifyDataSetChanged()
            //Log.i("size", "${model.list.value?.size ?: 0}")

            // Changed가 아니라 Inserted
            songAdapter.notifyItemRangeInserted(0,
                model.list.value?.size ?: 0)
        }

        model.requestSong()
    }

    //TODO: OnClickListener추가
    inner class SongAdapter: RecyclerView.Adapter<SongAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), OnClickListener {
//            val txTitle: TextView = itemView.findViewById(android.R.id.text1)
//            val txSinger: TextView = itemView.findViewById(android.R.id.text2)

            //TODO: 앞에 android 떼기
            val txTitle: TextView = itemView.findViewById(R.id.text1)
            val txSinger: TextView = itemView.findViewById(R.id.text2)
            val niImage: NetworkImageView = itemView.findViewById(R.id.image)


            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this) //왜 이렇게 가르쳐줄까? 왜 굳이 인터페이스를 구현? -> 메모리 관리 측면에서 이득
            }

            //TODO: OnClickListener 코드 작성
            override fun onClick(p0: View?) {
//                Toast.makeText(application,
//                "눌렸습니다 test",
//                Toast.LENGTH_LONG).show()

                val intent = Intent(application, SongActivity::class.java)
                val song = model.list.value?.get(adapterPosition)
                intent.putExtra(SongActivity.KEY_TITLE, song?.title)
                intent.putExtra(SongActivity.KEY_SINGER, song?.singer)
                intent.putExtra(SongActivity.KEY_IMAGE, model.getImageUrl(adapterPosition))
                startActivity(intent)
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //val view = layoutInflater.inflate(android.R.layout.simple_list_item_2,
            val view = layoutInflater.inflate(R.layout.item_song, //TODO:item_song으로 바꾸기
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txTitle.text = model.list.value?.get(position)?.title
            holder.txSinger.text = model.list.value?.get(position)?.singer
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader) //TODO: 이미지 url
        }

        override fun getItemCount() = model.list.value?.size ?: 0
    }
}