package bitcoin.wallet.modules.restore

import bitcoin.wallet.core.IKeyStoreSafeExecute
import bitcoin.wallet.core.managers.AdapterManager
import bitcoin.wallet.core.managers.WordsManager
import com.nhaarman.mockito_kotlin.KArgumentCaptor
import com.nhaarman.mockito_kotlin.argumentCaptor
import org.junit.Before
import org.junit.Test
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

class RestoreInteractorTest {

    private val wordsManager = Mockito.mock(WordsManager::class.java)
    private val delegate = Mockito.mock(RestoreModule.IInteractorDelegate::class.java)
    private val adapterManager = Mockito.mock(AdapterManager::class.java)
    private val keystoreSafeExecute = Mockito.mock(IKeyStoreSafeExecute::class.java)
    private val interactor = RestoreInteractor(wordsManager, adapterManager, keystoreSafeExecute)

    @Captor
    private val actionRunnableCaptor: KArgumentCaptor<Runnable> = argumentCaptor()

    @Captor
    private val successRunnableCaptor: KArgumentCaptor<Runnable> = argumentCaptor()

    @Captor
    private val failureRunnableCaptor: KArgumentCaptor<Runnable> = argumentCaptor()

    @Before
    fun before() {
        interactor.delegate = delegate
    }

    @Test
    fun restoreWallet_restore() {
        val words = listOf("first", "second", "etc")

        interactor.restore(words)

        verify(keystoreSafeExecute).safeExecute(actionRunnableCaptor.capture(), successRunnableCaptor.capture(), failureRunnableCaptor.capture())

        val actionRunnable = actionRunnableCaptor.firstValue
        val successRunnable = successRunnableCaptor.firstValue

        actionRunnable.run()
        successRunnable.run()

        verify(wordsManager).restore(words)
    }

    @Test
    fun restoreWallet_success() {
        val words = listOf("first", "second", "etc")

        interactor.restore(words)

        verify(keystoreSafeExecute).safeExecute(actionRunnableCaptor.capture(), successRunnableCaptor.capture(), failureRunnableCaptor.capture())

        val actionRunnable = actionRunnableCaptor.firstValue
        val successRunnable = successRunnableCaptor.firstValue

        actionRunnable.run()
        successRunnable.run()

        verify(delegate).didRestore()
        verify(wordsManager).isBackedUp = true
        verify(adapterManager).start()
        verifyNoMoreInteractions(delegate)
    }

    @Test
    fun restoreWallet_failureWordsError() {
        val words = listOf("first", "second", "etc")

        interactor.restore(words)

        verify(keystoreSafeExecute).safeExecute(actionRunnableCaptor.capture(), successRunnableCaptor.capture(), failureRunnableCaptor.capture())

        val actionRunnable = actionRunnableCaptor.firstValue
        val failureRunnable = failureRunnableCaptor.firstValue

        actionRunnable.run()
        failureRunnable.run()

        verify(delegate).didFailToRestore()
        verifyNoMoreInteractions(delegate)
    }

}
