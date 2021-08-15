package kz.sozdik.feedback.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnLifecycleDestroyed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kz.sozdik.R
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.feedback.di.DaggerFeedbackPresenterComponent
import kz.sozdik.presentation.utils.showToast
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class FeedbackFragment : MvpAppCompatFragment(), FeedbackView {

    companion object {
        fun create() = FeedbackFragment()
    }

    @InjectPresenter
    lateinit var feedbackPresenter: FeedbackPresenter

    @ProvidePresenter
    internal fun providePresenter(): FeedbackPresenter =
        DaggerFeedbackPresenterComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
            .getFeedbackPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnLifecycleDestroyed(viewLifecycleOwner))

            setContent {
                MaterialTheme(colors = lightColors(primary = Color(0xff3399ff))) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = getString(R.string.write_to_developers))
                                },
                                navigationIcon = {
                                    IconButton(onClick = { activity?.onBackPressed() }) {
                                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                },
                                backgroundColor = Color(0xff666666),
                                contentColor = Color(0xffffffff)
                            )
                        },
                        content = {
                            ContentComposable()
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun ContentComposable(sending: Boolean = false) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = stringResource(R.string.feedback_your_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(bottom = 8.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(R.string.feedback_your_email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(bottom = 8.dp))
            TextField(
                value = message,
                onValueChange = { message = it },
                label = { Text(text = stringResource(R.string.feedback_text)) },
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(bottom = 8.dp))
            if (sending) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { sendFeedback(name, email, message) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.feedback_send))
                }
            }
        }
    }

    private fun sendFeedback(name: String, email: String, message: String) {
        feedbackPresenter.sendFeedback(email, name, message)
    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun onFeedbackCreated() {
        showToast(R.string.feedback_message_sent)
        activity?.onBackPressed()
    }

    override fun showNameError(message: String?) {
//        nameInputLayout.error = message
    }

    override fun showEmailError(message: String?) {
//        emailInputLayout.error = message
    }

    override fun showMessageError(message: String?) {
//        messageInputLayout.error = message
    }

    override fun enableEmailEditText(isEnabled: Boolean) {
//        emailEditText.isEnabled = isEnabled
    }

    override fun enableNameEditText(isEnabled: Boolean) {
//        nameEditText.isEnabled = isEnabled
    }

    override fun setName(name: String) {
//        nameEditText.setText(name)
    }

    override fun setEmail(email: String) {
//        emailEditText.setText(email)
    }
}