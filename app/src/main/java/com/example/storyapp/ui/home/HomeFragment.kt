package com.example.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.data.Result
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.LoadingStateAdapter
import com.example.storyapp.ui.UserViewModel
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.detail.DetailActivity
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.onboarding.OnboardingActivity
import com.example.storyapp.utils.capitalized
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val userViewModel by viewModels<UserViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding as FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        binding.rvStories.layoutManager = layoutManager

        userViewModel.getUserSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLoggedIn) {
                startActivity(Intent(context, OnboardingActivity::class.java))
                requireActivity().finish()
            } else {
                getAllStories()
            }
        }
        
        binding.btnMaps.setOnClickListener {
            startActivity(Intent(context, MapsActivity::class.java))
        }
    }

    private fun getAllStories() {
        val adapter = StoryAdapter()

        binding.rvStories.adapter = adapter.apply {
            this.addLoadStateListener { state ->
               when(state.refresh) {
                    is LoadState.Loading -> {
                        showLoading(true)
                        showNotFound(false)
                    }
                    is LoadState.Error -> {
                        showLoading(false)
                        val error = (state.refresh as LoadState.Error).error
                        Snackbar.make(binding.root, error.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                    is LoadState.NotLoading -> {
                        showLoading(false)
                        showNotFound(itemCount == 0)
                    }
                }
            }
            
            this.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            
            this.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
                override fun onItemClicked(data: StoryEntity?) {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.DETAIL_ID, data?.id)
                    startActivity(intent)
                }
            })
        }
        
        homeViewModel.getAllStories().observe(viewLifecycleOwner) { result ->
            adapter.submitData(lifecycle, result)
        }
    }

    private fun showNotFound(show: Boolean) {
        with(binding) {
            bookImage.visibility = if (show) View.VISIBLE else View.GONE
            noStoryFoundText.visibility = if (show) View.VISIBLE else View.GONE
        }
    }
    private fun showLoading(loading: Boolean) {
        with(binding) {
            loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            overlayView.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}