package com.example.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.detail.DetailActivity
import com.example.storyapp.utils.capitalized
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel> {
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

        homeViewModel.getAllStories().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        val stories = result.data.listStory

                        binding.bookImage.visibility =
                            if (stories.isEmpty()) View.VISIBLE else View.GONE
                        binding.noStoryFoundText.visibility =
                            if (stories.isEmpty()) View.VISIBLE else View.GONE

                        stories.let { list ->
                            val adapter = StoryAdapter()
                            adapter.submitList(list)
                            binding.rvStories.adapter = adapter

                            adapter.setOnItemClickCallback(object :
                                StoryAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: ListStoryItem) {
                                    val intent = Intent(context, DetailActivity::class.java)
                                    intent.putExtra(DetailActivity.DETAIL_ID, data.id)
                                    startActivity(intent)
                                }
                            })
                        }

                        showLoading(false)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        binding.bookImage.visibility = View.VISIBLE
                        binding.noStoryFoundText.visibility = View.VISIBLE
                        Snackbar.make(
                            binding.root,
                            result.error.capitalized(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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