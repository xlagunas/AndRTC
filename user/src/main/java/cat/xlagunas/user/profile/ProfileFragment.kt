package cat.xlagunas.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.viewModel
import cat.xlagunas.user.R
import cat.xlagunas.user.User
import cat.xlagunas.user.databinding.ContentProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@OpenForTesting
class
ProfileFragment : Fragment() {
    private var _binding: ContentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profileViewModel = viewModel(ProfileViewModel::class.java)
        profileViewModel.user.observe(viewLifecycleOwner, Observer(this::onUserEvent))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ContentProfileBinding.inflate(layoutInflater, container, false)
        binding.logoutButton.setOnClickListener { profileViewModel.logout() }
        return binding.root
    }

    private fun onUserEvent(user: User?) {
        user?.let(this::renderUser)
    }

    private fun renderUser(user: User) {
        Glide.with(this)
            .load(user.imageUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.profile_placeholder))
            .into(binding.profileImage)

        binding.profileEmail.text = user.email
        binding.profileUsername.text = user.username
        binding.profileFirstname.text = user.firstName
        binding.profileLastname.text = user.lastName
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}