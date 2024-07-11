package com.example.calculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {


    private lateinit var workingstext: TextView
    private lateinit var resulttext: TextView
    private  var currnum:String = ""
    private lateinit var Currencies:List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
         workingstext  = findViewById<TextView>(R.id.workingsTV)
         resulttext = findViewById<TextView>(R.id.resultsTV)

        lifecycleScope.launch {
            // Perform API call on IO thread
             Currencies = CurrencyApi.getlistofCurr()
            Currencies.forEach { currency: String ->
                Log.d("",currency)
            }
            // Update UI on main thread
//                updateUI(exchangeRates)
        }
    }


    fun numberaction(view: View){

        if(view is Button){
            if(view.text=="." &&  '.' in currnum){
                workingstext.text = workingstext.text
            }else if(view.text=="." && currnum==""){
                workingstext.append("0"+view.text)
            }else if(view.text.isDigitsOnly() && currnum.isNotEmpty() && '.'  !in currnum && currnum.get(0)=='0'){
                workingstext.text = workingstext.text
            }
            else{
                workingstext.append(view.text)

                currnum+= view.text.toString()
                Log.d("tag",currnum)

            }
        }
    }
    fun Operatoraction(view: View){
        if(view is Button){
            if(workingstext.text.isEmpty()){
                workingstext.text = "0"+view.text
            }
            when(workingstext.text.last()){
                'x'-> workingstext.text = workingstext.text

                '/'-> workingstext.text = workingstext.text

                '-'-> workingstext.text = workingstext.text
                '+'-> workingstext.text = workingstext.text
                '^'-> workingstext.text = workingstext.text

                else-> {
                    currnum = ""
                    workingstext.append(view.text)


                }


            }
        }
    }
    fun allClearAction(view: View){
        workingstext.text=""
        resulttext.text=""
        currnum=""
    }
    fun backSpaceAction(view:View){
        val length = workingstext.length()
        val currlength = currnum.length



        if (length > 0) {
            workingstext.text = workingstext.text.subSequence(0, length - 1)

            if(currlength>0){
                currnum = currnum.substring(0,currlength-1)
                Log.d("tag",currnum.length.toString())
            }else{
                val symbols = "x/+-"
                if(workingstext.text.any { it in symbols }){
                    val lastSymbol = workingstext.text.findLast { it in symbols }.toString()
                    currnum = workingstext.text.toString().substringAfter(lastSymbol)
                }else{
                    currnum = workingstext.text.subSequence(0, length - 1).toString()
                }
            }

        }
    }
    fun equalsAction(view: View){
        if(view is Button){
            var text = workingstext.text.toString()
            text = text.replace('x','*')
            var result:String = Calculator(text).evaluateExpression()

            resulttext.text = result

        }

    }
    fun CurrConvAction(view:View){


        if(view is Button){
            var alertdialogbuilder = AlertDialog.Builder(this@MainActivity)
            alertdialogbuilder.setMessage("Convert Currency")
            alertdialogbuilder.setTitle("Currency Converter")
            alertdialogbuilder.setCancelable(true)
            alertdialogbuilder.setNegativeButton("Cancel"){
                negative,_->negative.dismiss()
            }

            var dialoglayout = layoutInflater.inflate(R.layout.dialog_layout,null)
            val firstspinner = dialoglayout.findViewById<Spinner>(R.id.spinner_firstConversion)
            val secondspinner = dialoglayout.findViewById<Spinner>(R.id.spinner_secondConversion)
            val arrayadapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Currencies)
            val converterbutton = dialoglayout.findViewById<Button>(R.id.buttonconvert)
            val editext = dialoglayout.findViewById<EditText>(R.id.et_firstConversion)
            val converttextview = dialoglayout.findViewById<TextView>(R.id.convertertextview)

            firstspinner.adapter = arrayadapter
            secondspinner.adapter = arrayadapter

            firstspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    CurrencyApi.basecurency = Currencies[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    CurrencyApi.basecurency = Currencies[0]
                }

            }
            secondspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    CurrencyApi.currencies = Currencies[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    CurrencyApi.currencies = Currencies[0]
                }

            }
            converterbutton.setOnClickListener {
                if(editext.text.toString()=="" ){
                    converttextview.text = "Give a number"
                }else{


                    CurrencyApi.amount = editext.text.toString()

                    Log.d("",CurrencyApi.basecurency+" "+CurrencyApi.currencies)
                    lifecycleScope.launch {
                        converttextview.text =  CurrencyApi.getcertaincurrency() +" "+CurrencyApi.currencies

                    }
                }
            }
            alertdialogbuilder.setView(dialoglayout)

            alertdialogbuilder.show()

            Log.d("",view.text.toString())
        }
    }
}