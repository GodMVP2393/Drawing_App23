package bigdaddy.god.kidsdrawingapp

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private var drawingView:DrawingView?=null
    private var mImageButtonCurrentPaint:ImageButton?=null
    var colorTag2:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView=findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val LinearLayoutPaintColors=findViewById<LinearLayout>(R.id.ll_paint_colors)

        mImageButtonCurrentPaint=LinearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

        val ibBrush: ImageButton = findViewById(R.id.ib_brush)
        ibBrush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        val ibErase: ImageButton = findViewById(R.id.ib_erase)
        ibErase.setOnClickListener{
            showBrushSizeChooserDialogErase()
        }

        val ibUndo: ImageButton = findViewById(R.id.ib_undo)
        ibUndo.setOnClickListener{
            drawingView?.onClickUndo()
        }

//        val ibRedo: ImageButton = findViewById(R.id.ib_redo)
//        ibRedo.setOnClickListener{
//            drawingView?.onClickRedo()
//        }

//        val ibSave: ImageButton = findViewById(R.id.ib_save)
//        ibSave.setOnClickListener{
//
//            if(isReadStorageAllowed()){
//                lifecycleScope.launch{
//                    val flDrawingView:LinearLayout=findViewById(R.id.drawing_view)
//
//                    saveBitmapFile(getBitmapFromView(flDrawingView))
//                }
//            }
//
//        }


    }

    private fun showBrushSizeChooserDialog(){
        val brushDialog= Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
           drawingView?.setColor(colorTag2)
            brushDialog.dismiss()
        }

        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            drawingView?.setColor(colorTag2)
            brushDialog.dismiss()
        }

        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            drawingView?.setColor(colorTag2)
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    private fun showBrushSizeChooserDialogErase(){
        val brushDialog1= Dialog(this)
        brushDialog1.setContentView(R.layout.dialog_brush_size)
        brushDialog1.setTitle("Brush Size: ")
        val smallBtn: ImageButton = brushDialog1.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            val colorTag1="#FFFFFF"
            drawingView?.setColor(colorTag1)
            brushDialog1.dismiss()
        }

        val mediumBtn: ImageButton = brushDialog1.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            val colorTag1="#FFFFFF"
            drawingView?.setColor(colorTag1)
            brushDialog1.dismiss()
        }

        val largeBtn: ImageButton = brushDialog1.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            val colorTag1="#FFFFFF"
            drawingView?.setColor(colorTag1)
            brushDialog1.dismiss()
        }

        brushDialog1.show()
    }

    fun paintClicked(view: View){
        if(view!==mImageButtonCurrentPaint){
            val imageButton=view as ImageButton
            val colorTag=imageButton.tag.toString()
            colorTag2=colorTag
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
            )

            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )

            mImageButtonCurrentPaint=view
        }
    }

    private fun isReadStorageAllowed():Boolean{
        val result=ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE)

        return result==PackageManager.PERMISSION_GRANTED
    }

    private fun getBitmapFromView(view:View): Bitmap {
        val returnedBitmap=Bitmap.createBitmap(view.width,view.height,
            Bitmap.Config.ARGB_8888)

        val canvas= Canvas(returnedBitmap)
        val bgDrawable=view.background
        if(bgDrawable!=null) {
            bgDrawable.draw(canvas)
        }
        else {canvas.drawColor(Color.WHITE)}

        view.draw(canvas)

        return returnedBitmap
    }



    private suspend fun saveBitmapFile(mBitmap:Bitmap?):String{
        var result=""
        withContext(Dispatchers.IO){
            if(mBitmap!=null){
                try{
                    val bytes= ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                    val f= File(externalCacheDir?.absoluteFile.toString()
                            + File.separator + "DrawingApp_" + System.currentTimeMillis()/1000 +".png"
                    )

                    val fo= FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result=f.absolutePath

                    runOnUiThread{
                        if(result.isNotEmpty()){
                            Toast.makeText(
                                this@MainActivity,
                                "File Saved Successfully:$result",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        else{
                            Toast.makeText(
                                this@MainActivity,
                                "Something went wrong while saving the file",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                catch (e:Exception){
                    result=" "
                    e.printStackTrace()
                }
            }
        }
        return result
    }


}