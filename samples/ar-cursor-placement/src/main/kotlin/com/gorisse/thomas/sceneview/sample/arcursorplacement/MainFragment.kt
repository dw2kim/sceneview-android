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

    lateinit var cursorNode: CursorNode
    var modelNodeList: MutableList<ArNode>? = null

    var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
            actionButton.isGone = value
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

        cursorNode = CursorNode(context = requireContext(), coroutineScope = lifecycleScope)
        cursorNode.onTrackingChanged = { _, isTracking ->
            if (!isLoading) {
                actionButton.isGone = !isTracking
            }
        }
        sceneView.addChild(cursorNode)
    }

    fun actionButtonClicked(view: View? = null) {
        // TODO: 1. [app] Place the multiple models (Daewon)

        // TODO: 2. [app] Set up the WebRTC in the app side (Shuo)
        //     TEST: Use the RoomID to join the WebRTC

        // TODO: 3. [web] P2P - Send the JSON file (Shuyue) -
        //      ON Demo, explain that we only use P2P for the coordinates! (While joining the room, secure)

        // TODO: 4. [web] Normalize the cord (Nina)

        // TODO: 5. ADD: Action & ModelName & Coordinate
        //  {
        //    type: "add" or "remove",
        //    data: {
        //        id: string
        //        key: "technician" or "hammer" or "arrow"
        //        x: number in range of 0 to 1
        //        y: number in range of 0 to 1
        //    }
        //}

        // TODO: 6. REMOVE

        // TODO: 7. Clean up all the models

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
                    isLoading = false
                }
            )
            sceneView.addChild(this)
        }
        modelNodeList?.add(newCursorNode as ArNode)
    }
}