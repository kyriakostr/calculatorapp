package com.example.calculator

import android.util.Log
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class Calculator(var expression:String) {

     fun evaluateExpression(): String {

         try {
             if(!expression.last().isDigit() && expression.last()!='.'){
                 return "Add a digit"
             }

             var result =  ExpressionBuilder(expression).build().evaluate().toString()

             if(result.length>20 ){
                 Log.d("",result.length.toString())
                 return  DecimalFormat("0.####E0").format(result.toDouble())
             }else{

//                 result =  DecimalFormat("#,###.###")
//                     .format(result.toDouble())
                 return NumberFormat.getNumberInstance(Locale.US).format(result.toDouble())
             }

         }catch ( e :ArithmeticException ){

             return  e.message.toString()
         }


    }

}