package io.github.pknujsp.everyweather.core.model.flickr

sealed interface FlickrGalleryId {
    val sunriseId: String
    val sunsetId: String
    val dayId: String
    val nightId: String

    data object Clear : FlickrGalleryId {
        override val sunriseId = "72157719913955346"
        override val sunsetId = "72157719931028631"
        override val dayId = "72157719980390655"
        override val nightId = "72157719931035301"
    }

    data object PartlyCloudy : FlickrGalleryId {
        override val sunriseId = "72157719931023221"
        override val sunsetId = "72157719980387665"
        override val dayId = "72157719938096007"
        override val nightId = "72157719927362534"
    }

    data object MostlyCloudy : FlickrGalleryId {
        override val sunriseId = "72157719938087287"
        override val sunsetId = "72157719925493763"
        override val dayId = "72157719938096492"
        override val nightId = "72157719925500593"
    }

    data object Overcast : FlickrGalleryId {
        override val sunriseId = "72157719938089657"
        override val sunsetId = "72157719925494163"
        override val dayId = "72157719938096892"
        override val nightId = "72157719938100367"
    }

    data object Rain : FlickrGalleryId {
        override val sunriseId = "72157719938090082"
        override val sunsetId = "72157719931030491"
        override val dayId = "72157719931034166"
        override val nightId = "72157719980396000"
    }

    data object Snow : FlickrGalleryId {
        override val sunriseId = "72157719938090672"
        override val sunsetId = "72157719925495538"
        override val dayId = "72157719938097657"
        override val nightId = "72157719931038496"
    }
}
