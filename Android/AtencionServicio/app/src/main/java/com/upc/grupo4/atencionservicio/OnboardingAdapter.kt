package com.upc.grupo4.atencionservicio

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        OnboardingFragment.newInstance(
            R.drawable.ic_intro1,
            "Bienvenido",
            "Aquí podrás gestionar tus servicios de manera rápida y sencilla."
        ),
        OnboardingFragment.newInstance(
            R.drawable.ic_intro2,
            "Soporte 24/7",
            "Nuestro equipo siempre estará disponible para ayudarte."
        ),
        OnboardingFragment.newInstance(
            R.drawable.ic_intro3,
            "Notificaciones",
            "Recibe alertas y recordatorios importantes en tiempo real."
        )
    )

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}
