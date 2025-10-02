package com.upc.grupo4.atencionservicio

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        OnboardingFragment.newInstance(
            R.drawable.ic_intro1,
            "Tracking",
            "Podrás atender y hacer seguimiento en línea de tus servicios asignados."
        ),
        OnboardingFragment.newInstance(
            R.drawable.ic_intro2,
            "Registro fotográfico",
            "Luego de realizar el servicio, toma hasta 4 fotos como evidencia y guardálas en el aplicativo."
        ),
        OnboardingFragment.newInstance(
            R.drawable.ic_intro3,
            "Registro de datos",
            "Podrás registrar los datos del cliente o usuario designado. Nombres, documentos de identidad y firma."
        )
    )

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}
