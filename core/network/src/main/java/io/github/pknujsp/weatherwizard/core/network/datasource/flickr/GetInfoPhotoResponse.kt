package io.github.pknujsp.weatherwizard.core.network.datasource.flickr


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetInfoPhotoResponse(
    @SerialName("photo") val photo: Photo = Photo(),
    @SerialName("stat") val stat: String = "" // ok
) {

    val imageUrl: String = """
        https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.originalSecret.ifEmpty { photo.secret }}${if (photo.originalSecret.isEmpty()) "_b.jpg" else "_o.jpg"}
        """.trimIndent()

    @Serializable
    data class Photo(
        @SerialName("dateuploaded") val dateuploaded: String = "", // 1548444483
        @SerialName("farm") val farm: Int = 0, // 5
        @SerialName("id") val id: String = "", // 32999137498
        @SerialName("isfavorite") val isfavorite: Int = 0, // 0
        @SerialName("license") val license: String = "", // 4
        @SerialName("media") val media: String = "", // photo
        @SerialName("originalformat") val originalformat: String = "", // jpg
        @SerialName("originalsecret") val originalSecret: String = "", // f235c29542
        @SerialName("owner") val owner: Owner = Owner(),
        @SerialName("people") val people: People = People(),
        @SerialName("secret") val secret: String = "", // 774df2c9ee
        @SerialName("server") val server: String = "", // 4815
        @SerialName("tags") val tags: Tags = Tags(),
        @SerialName("urls") val urls: Urls = Urls(),
    ) {
        @Serializable
        data class Comments(
            @SerialName("_content") val content: String = "" // 0
        )

        @Serializable
        data class Dates(
            @SerialName("lastupdate") val lastupdate: String = "", // 1548444490
            @SerialName("posted") val posted: String = "", // 1548444483
            @SerialName("taken") val taken: String = "", // 2018-09-09 20:13:01
            @SerialName("takengranularity") val takengranularity: Int = 0, // 0
            @SerialName("takenunknown") val takenunknown: String = "" // 0
        )

        @Serializable
        data class Description(
            @SerialName("_content") val content: String = "" // scenaries
        )

        @Serializable
        data class Editability(
            @SerialName("canaddmeta") val canaddmeta: Int = 0, // 0
            @SerialName("cancomment") val cancomment: Int = 0 // 0
        )

        @Serializable
        data class Geoperms(
            @SerialName("iscontact") val iscontact: Int = 0, // 0
            @SerialName("isfamily") val isfamily: Int = 0, // 0
            @SerialName("isfriend") val isfriend: Int = 0, // 0
            @SerialName("ispublic") val ispublic: Int = 0 // 1
        )

        @Serializable
        data class Location(
            @SerialName("accuracy") val accuracy: String = "", // 16
            @SerialName("context") val context: String = "", // 0
            @SerialName("country") val country: Country = Country(),
            @SerialName("county") val county: County = County(),
            @SerialName("latitude") val latitude: String = "", // 37.553045
            @SerialName("locality") val locality: Locality = Locality(),
            @SerialName("longitude") val longitude: String = "", // 127.034727
            @SerialName("neighbourhood") val neighbourhood: Neighbourhood = Neighbourhood(),
            @SerialName("region") val region: Region = Region()
        ) {
            @Serializable
            data class Country(
                @SerialName("_content") val content: String = "", // 한국
                @SerialName("woeid") val woeid: Int = 0 // 23424868
            )

            @Serializable
            data class County(
                @SerialName("_content") val content: String = "", // 성동구
                @SerialName("woeid") val woeid: Int = 0 // 28289286
            )

            @Serializable
            data class Locality(
                @SerialName("_content") val content: String = "", // 서울
                @SerialName("woeid") val woeid: Int = 0 // 1132599
            )

            @Serializable
            data class Neighbourhood(
                @SerialName("_content") val content: String = "", // 왕십리
                @SerialName("woeid") val woeid: Int = 0 // 28835051
            )

            @Serializable
            data class Region(
                @SerialName("_content") val content: String = "", // 서울
                @SerialName("woeid") val woeid: Int = 0 // 20069923
            )
        }



        @Serializable
        data class Owner(
            @SerialName("gift") val gift: Gift = Gift(),
            @SerialName("iconfarm") val iconfarm: Int = 0, // 8
            @SerialName("iconserver") val iconserver: String = "", // 7087
            @SerialName("location") val location: String = "", // null
            @SerialName("nsid") val nsid: String = "", // 73479994@N00
            @SerialName("path_alias") val pathAlias: String = "", // fri13th
            @SerialName("realname") val realname: String = "",
            @SerialName("username") val username: String = "" // lazy fri13th
        ) {
            @Serializable
            data class Gift(
                @SerialName("eligible_durations") val eligibleDurations: List<String> = listOf(),
                @SerialName("gift_eligible") val giftEligible: Boolean = false, // true
                @SerialName("new_flow") val newFlow: Boolean = false // true
            )
        }

        @Serializable
        data class People(
            @SerialName("haspeople") val haspeople: Int = 0 // 0
        )

        @Serializable
        data class Publiceditability(
            @SerialName("canaddmeta") val canaddmeta: Int = 0, // 0
            @SerialName("cancomment") val cancomment: Int = 0 // 1
        )

        @Serializable
        data class Tags(
            @SerialName("tag") val tag: List<Tag> = listOf()
        ) {
            @Serializable
            data class Tag(
                @SerialName("author") val author: String = "", // 73479994@N00
                @SerialName("authorname") val authorname: String = "", // lazy fri13th
                @SerialName("_content") val content: String = "", // tour
                @SerialName("id") val id: String = "", // 2972730-32999137498-3768
                @SerialName("machine_tag") val machineTag: String = "",
                @SerialName("raw") val raw: String = "" // tour
            )
        }

        @Serializable
        data class Title(
            @SerialName("_content") val content: String = "" // scenaries 201809
        )

        @Serializable
        data class Urls(
            @SerialName("url") val url: List<Url> = listOf()
        ) {
            @Serializable
            data class Url(
                @SerialName("_content") val content: String = "", // https://www.flickr.com/photos/fri13th/32999137498/
                @SerialName("type") val type: String = "" // photopage
            )
        }

        @Serializable
        data class Usage(
            @SerialName("canblog") val canblog: Int = 0, // 0
            @SerialName("candownload") val candownload: Int = 0, // 1
            @SerialName("canprint") val canprint: Int = 0, // 0
            @SerialName("canshare") val canshare: Int = 0 // 1
        )

        @Serializable
        data class Visibility(
            @SerialName("isfamily") val isfamily: Int = 0, // 0
            @SerialName("isfriend") val isfriend: Int = 0, // 0
            @SerialName("ispublic") val ispublic: Int = 0 // 1
        )
    }
}