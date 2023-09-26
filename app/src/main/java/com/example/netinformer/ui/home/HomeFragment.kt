package com.example.netinformer.ui.home

import android.Manifest
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.telephony.CarrierConfigManager
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
//import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.netinformer.databinding.FragmentHomeBinding
import java.net.NetworkInterface

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    fun getNetworkInterfaces(): String{
        val en = NetworkInterface.getNetworkInterfaces();
        var res = ""
        while (en.hasMoreElements()){
            var nif =en.nextElement()
            if(nif.interfaceAddresses.toString().contains(".") && nif.name != "lo" ){

                val ipAddress = nif.interfaceAddresses.toString().split("/").find { it.contains(".") };
                res += "${nif.name}\n" +
                        "- interfaceAddresses:  ${ipAddress} \n\n";
            }
        }

        return res+"\n\n"
    }


    fun getConnectivities(): String {
        val connectivityManager =  requireActivity()?.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager?.activeNetwork;
        val caps = connectivityManager?.getNetworkCapabilities(currentNetwork)
        val linkProperties = connectivityManager?.getLinkProperties(currentNetwork)
        return linkProperties.toString()

//        var res = ""
//        linkProperties

    }

    fun getTelephonyInfo(): String {
        val ctx = requireActivity();
        val telephonyManager =  ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;

        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return "no permission"
        }

        var res = ""

        telephonyManager.allCellInfo.forEach {
            val cell = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                res +="----\n" +
                    "${cell.cellIdentity} \n" +
                        "${cell.cellConnectionStatus}\n" +
                        "${cell.cellSignalStrength}\n" +
                        "registred: ${cell.isRegistered}, timestamp: ${cell.timestampMillis} \n" +
                    "\n\n"
            } else {
                res = "buy a newer phone"
            }


        }

        return res
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        textView.movementMethod = ScrollingMovementMethod()



        homeViewModel.text.observe(viewLifecycleOwner) {
            Log.i("LOGGER", "homeViewModel.text.observe"+viewLifecycleOwner.toString());
            textView.text = it

            textView.text = "NET INTERFACES:\n"+getNetworkInterfaces() + "\n\nCONNECTIVITES\n" + getConnectivities() +
                    "\n\nCONNECTIVITES\n"+getTelephonyInfo() ;



        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}