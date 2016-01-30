package lt.vilnius.tvarkau.network;

import dagger.Component;
import lt.vilnius.tvarkau.ProblemDetailFragment;

/**
 * Created by Karolis Vycius on 2016-01-30.
 */
@Component(modules = APIModule.class)
public interface APIComponent {
    void inject(ProblemDetailFragment fragment);
}
