package kz.sozdik.feedback.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import javax.inject.Inject
import kz.sozdik.R
import kz.sozdik.core.system.ViewModelFactory
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.feedback.di.DaggerFeedbackComponent
import kz.sozdik.presentation.utils.showToast
import moxy.MvpAppCompatFragment

class FeedbackFragment : Fragment() {

    companion object {
        fun create() = FeedbackFragment()
    }

    @Inject
    lateinit var factory: ViewModelFactory<FeedbackViewModel>

    override fun onAttach(context: Context) {
        DaggerFeedbackComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
            .inject(this)

        super.onAttach(context)
    }

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
    fun ContentComposable() {
        var name by remember { mutableStateOf(TextFieldValue()) }
        var email by remember { mutableStateOf(TextFieldValue()) }
        var message by remember { mutableStateOf(TextFieldValue()) }

        val viewModel: FeedbackViewModel by viewModels { factory }
        val feedbackState by viewModel.feedbackState.collectAsState()

        LaunchedEffect(feedbackState) {
            when (feedbackState) {
                FeedbackViewState.Sent -> showToast("Sent")
                is FeedbackViewState.Error -> showToast((feedbackState as FeedbackViewState.Error).message)
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            NameTextField(textState = name, onTextChanged = { name = it }, feedbackState)

            Spacer(Modifier.padding(bottom = 8.dp))

            EmailTextField(textState = email, onTextChanged = { email = it }, feedbackState)

            Spacer(Modifier.padding(bottom = 8.dp))

            ContentTextField(textState = message, onTextChanged = { message = it }, feedbackState)

            Spacer(Modifier.padding(bottom = 8.dp))

            if (feedbackState is FeedbackViewState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.onSendClicked(name.text, email.text, message.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.feedback_send))
                }
            }
        }
    }

    @Composable
    fun NameTextField(
        textState: TextFieldValue,
        onTextChanged: (TextFieldValue) -> Unit,
        feedbackState: FeedbackViewState
    ) {
        TextField(
            value = textState,
            onValueChange = { onTextChanged(it) },
            label = { Text(text = stringResource(R.string.feedback_your_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = feedbackState is FeedbackViewState.NameError
        )
    }

    @Composable
    fun EmailTextField(
        textState: TextFieldValue,
        onTextChanged: (TextFieldValue) -> Unit,
        feedbackState: FeedbackViewState
    ) {
        TextField(
            value = textState,
            onValueChange = { onTextChanged(it) },
            label = { Text(text = stringResource(R.string.feedback_your_email)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = feedbackState is FeedbackViewState.EmailError
        )
    }

    @Composable
    fun ContentTextField(
        textState: TextFieldValue,
        onTextChanged: (TextFieldValue) -> Unit,
        feedbackState: FeedbackViewState
    ) {
        TextField(
            value = textState,
            onValueChange = { onTextChanged(it) },
            label = { Text(text = stringResource(R.string.feedback_text)) },
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
            isError = feedbackState is FeedbackViewState.ContentError
        )
    }

//    override fun onError(message: String) {
//        showToast(message)
//    }
//
//    override fun onFeedbackCreated() {
//        showToast(R.string.feedback_message_sent)
//        activity?.onBackPressed()
//    }
//
//    override fun showNameError(message: String?) {
////        nameInputLayout.error = message
//    }
//
//    override fun showEmailError(message: String?) {
////        emailInputLayout.error = message
//    }
//
//    override fun showMessageError(message: String?) {
////        messageInputLayout.error = message
//    }
//
//    override fun enableEmailEditText(isEnabled: Boolean) {
////        emailEditText.isEnabled = isEnabled
//    }
//
//    override fun enableNameEditText(isEnabled: Boolean) {
////        nameEditText.isEnabled = isEnabled
//    }
//
//    override fun setName(name: String) {
////        nameEditText.setText(name)
//    }
//
//    override fun setEmail(email: String) {
////        emailEditText.setText(email)
//    }
}