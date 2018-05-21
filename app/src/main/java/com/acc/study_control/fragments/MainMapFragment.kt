package com.acc.study_control.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.acc.study_control.R
import com.acc.study_control.utils.RequestPermissionsUtil
import com.acc.study_control.utils.SingletonHolder

class MainMapFragment: Fragment(), OnMapReadyCallback {

    private var mMapView: MapView? = null
    private var mMap: GoogleMap? = null
    private var parentActivity: Activity? = null

    /**
     * Singleton Implementation
     */
    companion object : SingletonHolder<MainMapFragment>(::MainMapFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.maps, container, false)
        parentActivity = activity

        mMapView = view.findViewById(R.id.mapView) as MapView
        mMapView?.onCreate(savedInstanceState)
        mMapView?.onResume()

        MapsInitializer.initialize(parentActivity?.applicationContext)

        mMapView?.getMapAsync(this)

        return view
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0

        setCenterPositionControls()
    }

    private fun setCenterPositionControls() {
        // Set on City Center
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-17.783551, -63.181746), 13f))

        val permission = ContextCompat.checkSelfPermission(parentActivity!!,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true
        } else {
            RequestPermissionsUtil.requestPermissions(parentActivity!!)
        }
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onDestroy() {
        mMapView?.onDestroy()
        super.onDestroy()
    }
}