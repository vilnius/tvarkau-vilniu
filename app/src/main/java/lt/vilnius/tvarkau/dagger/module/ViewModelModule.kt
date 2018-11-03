package lt.vilnius.tvarkau.dagger.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import lt.vilnius.tvarkau.viewmodel.LoginViewModel
import lt.vilnius.tvarkau.viewmodel.MyReportListViewModel
import lt.vilnius.tvarkau.viewmodel.NewReportViewModel
import lt.vilnius.tvarkau.viewmodel.ReportDetailsViewModel
import lt.vilnius.tvarkau.viewmodel.ReportFilterViewModel
import lt.vilnius.tvarkau.viewmodel.ReportListViewModel
import lt.vilnius.tvarkau.viewmodel.ReportTypeListViewModel
import lt.vilnius.tvarkau.viewmodel.ViewModelFactory
import lt.vilnius.tvarkau.viewmodel.ViewModelKey

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ReportListViewModel::class)
    internal abstract fun postListViewModel(viewModel: ReportListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReportDetailsViewModel::class)
    internal abstract fun reportDetailsViewModel(viewModel: ReportDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReportTypeListViewModel::class)
    internal abstract fun reportTypeListViewModel(viewModel: ReportTypeListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewReportViewModel::class)
    internal abstract fun newReportViewModel(viewModel: NewReportViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyReportListViewModel::class)
    internal abstract fun myReportsListViewModel(viewModel: MyReportListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReportFilterViewModel::class)
    internal abstract fun reportFilterViewModel(viewModel: ReportFilterViewModel): ViewModel
}
