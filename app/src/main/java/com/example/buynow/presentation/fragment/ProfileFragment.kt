package com.example.buynow.presentation.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.buynow.NotificationActivity
import com.example.buynow.R
import com.example.buynow.aboutus
import com.example.buynow.data.local.room.Card.CardViewModel
import com.example.buynow.data.model.User
import com.example.buynow.presentation.activity.*
import com.example.buynow.utils.FirebaseUtils.storageReference
import com.google.android.gms.tasks.Continuation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var profileImage: CircleImageView
    private lateinit var uploadImageButton: Button
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var animationView: LottieAnimationView
    private lateinit var linearLayout2: LinearLayout
    private lateinit var linearLayout3: LinearLayout
    private lateinit var linearLayout4: LinearLayout
    private lateinit var cardViewModel: CardViewModel

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var cards = 0

    private val userCollectionRef = Firebase.firestore.collection("Users")
    private val firebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Bind views
        profileImage = view.findViewById(R.id.profileImage_profileFrag)
        uploadImageButton = view.findViewById(R.id.uploadImage_profileFrag)
        profileName = view.findViewById(R.id.profileName_profileFrag)
        profileEmail = view.findViewById(R.id.profileEmail_profileFrag)
        animationView = view.findViewById(R.id.animationView)
        linearLayout2 = view.findViewById(R.id.linearLayout2)
        linearLayout3 = view.findViewById(R.id.linearLayout3)
        linearLayout4 = view.findViewById(R.id.linearLayout4)

        val settingsCard = view.findViewById<CardView>(R.id.settingCd_profileFrag)
        val notification = view.findViewById<CardView>(R.id.notification_page)
        val paymentCard = view.findViewById<CardView>(R.id.paymentMethod_ProfilePage)
        val aboutUsCard = view.findViewById<CardView>(R.id.aboutUsCard_ProfilePage)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val cardsNumber = view.findViewById<TextView>(R.id.cardsNumber_profileFrag)

        // ViewModel setup
        cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)
        cardViewModel.allCards.observe(viewLifecycleOwner) {
            cards = it.size
            cardsNumber.text = if (cards == 0) {
                "You have no cards."
            } else {
                "You have $cards card(s)."
            }
        }

        // Navigation
        aboutUsCard.setOnClickListener {
            startActivity(Intent(context, aboutus::class.java))
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        notification.setOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }

        paymentCard.setOnClickListener {
            startActivity(Intent(context, PaymentMethodActivity::class.java))
        }

        settingsCard.setOnClickListener {
            startActivity(Intent(context, SettingsActivity::class.java))
        }

        profileImage.setOnClickListener {
            val popupMenu = PopupMenu(context, profileImage)
            popupMenu.menuInflater.inflate(R.menu.profile_photo_storage, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.galleryMenu -> launchGallery()
                    R.id.cameraMenu -> uploadImage()
                }
                true
            }
            popupMenu.show()
        }

        uploadImageButton.setOnClickListener {
            uploadImage()
        }

        uploadImageButton.visibility = View.GONE
        hideLayout()
        getUserData()

        return view
    }

    private fun hideLayout() {
        animationView.playAnimation()
        animationView.loop(true)
        animationView.visibility = View.VISIBLE
        linearLayout2.visibility = View.GONE
        linearLayout3.visibility = View.GONE
        linearLayout4.visibility = View.GONE
    }

    private fun showLayout() {
        animationView.pauseAnimation()
        animationView.visibility = View.GONE
        linearLayout2.visibility = View.VISIBLE
        linearLayout3.visibility = View.VISIBLE
        linearLayout4.visibility = View.VISIBLE
    }

    private fun getUserData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val snapshot = userCollectionRef.document(firebaseAuth.uid!!).get().await()
            val user = snapshot.toObject(User::class.java)

            withContext(Dispatchers.Main) {
                user?.let {
                    profileName.text = it.userName
                    profileEmail.text = it.userEmail

                    Glide.with(this@ProfileFragment)
                        .load(it.userImage)
                        .placeholder(R.drawable.ic_profile)
                        .into(profileImage)
                }
                showLayout()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
                showLayout()
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage() {
        if (filePath == null) {
            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = storageReference.child("profile_Image/${UUID.randomUUID()}")
        val uploadTask = ref.putFile(filePath!!)

        uploadTask.continueWithTask(Continuation { task ->
            if (!task.isSuccessful) task.exception?.let { throw it }
            ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uri = task.result.toString()
                addUploadRecordToDb(uri)
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUploadRecordToDb(uri: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            userCollectionRef.document(firebaseAuth.uid!!).update("userImage", uri).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
                getUserData()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            filePath = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)
                profileImage.setImageBitmap(bitmap)
                uploadImageButton.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun logoutUser() {
        firebaseAuth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
