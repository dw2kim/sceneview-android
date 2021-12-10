package com.gorisse.thomas.sceneview.sample.arcursorplacement

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.CursorNode
import io.github.sceneview.utils.doOnApplyWindowInsets

class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var sceneView: ArSceneView
    lateinit var loadingView: View
    lateinit var actionButton: ExtendedFloatingActionButton
    lateinit var deleteObjectButton: ExtendedFloatingActionButton


    lateinit var cursorNode: CursorNode
    var modelNodeList: MutableList<ArNode> = mutableListOf<ArNode>()


    var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
            actionButton.isGone = value
            deleteObjectButton.isGone = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById(R.id.sceneView)
        loadingView = view.findViewById(R.id.loadingView)
        actionButton = view.findViewById<ExtendedFloatingActionButton>(R.id.actionButton).apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener(::actionButtonClicked)
        }
        deleteObjectButton = view.findViewById<ExtendedFloatingActionButton>(R.id.deleteObjectButton).apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin + 200
            }
            setOnClickListener(::deleteObjectButtonClicked)
        }

        cursorNode = CursorNode(context = requireContext(), coroutineScope = lifecycleScope)
        cursorNode.onTrackingChanged = { _, isTracking ->
            if (!isLoading) {
                actionButton.isGone = !isTracking
            }
        }
        sceneView.addChild(cursorNode)
    }

    private fun actionButtonClicked(view: View? = null) {
        val newCursorNode = cursorNode.createAnchoredNode()?.apply {
            scale = 0.1F
            isLoading = true
            setModel(
                context = requireContext(),
                coroutineScope = lifecycleScope,
                glbFileLocation = "models/vt_colored_1.glb",
                onLoaded = {
                    actionButton.text = getString(R.string.place_object)
                    actionButton.icon = resources.getDrawable(R.drawable.ic_target)


                    deleteObjectButton.text = getString(R.string.delete_object)
                    deleteObjectButton.icon = resources.getDrawable(R.drawable.ic_target)
                    isLoading = false
                }
            )
            sceneView.addChild(this)
        }
        modelNodeList.add(newCursorNode as ArNode)
    }


    private fun deleteObjectButtonClicked(view: View? = null) {

        for( modelNode in modelNodeList) {
            modelNode.destroy()
        }

        modelNodeList.clear()
    }
}