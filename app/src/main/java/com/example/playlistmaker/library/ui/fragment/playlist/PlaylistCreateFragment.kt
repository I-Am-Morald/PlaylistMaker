package com.example.playlistmaker.library.ui.fragment.playlist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreateBinding
import com.example.playlistmaker.library.ui.view_model.PlaylistCreateViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateFragment : Fragment() {
    private var _binding: FragmentPlaylistCreateBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val viewModel: PlaylistCreateViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    imageUri = uri
                    Glide.with(this)
                        .load(uri)
                        .centerCrop()
                        .into(binding.addCover)
                }
            }

        binding.createButton.isEnabled = false

        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialog)
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_message)
            .setNeutralButton(R.string.dialog_cancel) { dialog, which -> }
            .setPositiveButton(R.string.dialog_confirm) { dialog, which -> findNavController().popBackStack() }

        val playlistNameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.playlistName.text.isNotBlank()) {
                    binding.createButton.isEnabled = true
                    binding.createButton.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.YP_blue)
                    )
                } else {
                    binding.createButton.isEnabled = false
                    binding.createButton.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.YP_text_gray))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }

        binding.playlistName.addTextChangedListener(playlistNameTextWatcher)

        binding.addCoverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButton.setOnClickListener {
            backPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        })

        binding.createButton.setOnClickListener {
            val playlistName = binding.playlistName.text
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, playlistName),
                Toast.LENGTH_SHORT
            ).show()
            val name = binding.playlistName.text.toString()
            val description = binding.playlistDescription.text.toString()
            val path = imageUri?.let { saveImageToPrivateStorage(it, name) }
            viewModel.addPlaylist(name, description, path)
            findNavController().popBackStack()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, name: String): String {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.playlist_dir)
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "$name ${System.currentTimeMillis()}")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath
    }

    private fun backPressed() {
        if (binding.playlistName.text.isNotEmpty() || binding.playlistDescription.text.isNotEmpty() || binding.addCover.drawable != null) {
            confirmDialog.show()
        } else {
        findNavController().popBackStack()}
    }

}
