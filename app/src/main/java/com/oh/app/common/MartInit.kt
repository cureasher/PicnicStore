package com.oh.app.common

import com.oh.app.data.mart.MartRow
import net.daum.mf.map.api.MapPoint

/**
 * openAPI 제공하지 않아서 하드코딩함
 * 마트 초기 데이터
 */
class MartInit {
    companion object {
        private lateinit var martData: HashMap<String, ArrayList<String>>
        fun setMartMap(): HashMap<String, ArrayList<String>> {
            martData = HashMap()
            martData["종로구"] = arrayListOf("광장시장", "통인시장", "롯데백화점 명동본점", "신세계백화점 본점")
            martData["중구"] = arrayListOf("서울중앙시장", "남대문시장", "이마트 청계천점", "롯데마트 서울역점")
            martData["용산구"] = arrayListOf("후암시장", "용문시장", "농협하나로마트 용산점", "이마트 용산점")
            martData["성동구"] = arrayListOf("금남시장", "뚝도시장", "이마트 성수점", "이마트 왕십리점")
            martData["광진구"] = arrayListOf("노룬산골목시장", "자양골목시장", "롯데마트 강변점", "이마트 자양점")
            martData["동대문구"] = arrayListOf("경동시장", "청량리종합시장", "롯데백화점 청량리점", "홈플러스 동대문점")
            martData["중랑구"] = arrayListOf("동원시장", "우림시장", "홈플러스 면목점", "이마트 상봉점")
            martData["성북구"] = arrayListOf("장위전통시장", "돈암제일시장", "현대백화점 미아점", "이마트 미아점")
            martData["강북구"] = arrayListOf("숭인시장", "수유재래시장", "농협하나로마트 미아점", "롯데백화점 미아점")
            martData["도봉구"] = arrayListOf("신창시장", "방학동 도깨비시장", "이마트 창동점", "홈플러스 방학점")
            martData["노원구"] = arrayListOf("상계중앙시장", "공릉동 도깨비시장", "홈플러스 중계점", "롯데백화점 노원점")
            martData["은평구"] = arrayListOf("대조시장", "대림시장", "이마트 은평점", "NC백화점 불광점")
            martData["서대문구"] = arrayListOf("독립문영천시장", "인왕시장", "롯데슈퍼 유진점", "현대백화점 신촌점")
            martData["마포구"] = arrayListOf("망원시장", "마포농수산물시장", "농협하나로마트 신촌점", "홈플러스 월드컵점")
            martData["양천구"] = arrayListOf("신영시장", "목3동시장", "홈플러스 목동점", "이마트 목동점")
            martData["강서구"] = arrayListOf("화곡본동시장", "송화시장", "홈플러스 익스프레스 등촌점")
            martData["구로구"] = arrayListOf("남구로시장", "고척근린시장", "이마트 신도림점", "NC백화점 신구로점")
            martData["금천구"] = arrayListOf("별빛남문시장", "현대시장", "홈플러스 독산점", "홈플러스 시흥점")
            martData["영등포구"] = arrayListOf("영등포 전통시장", "대림중앙시장", "이마트 여의도점", "홈플러스 영등포점")
            martData["동작구"] = arrayListOf("남성시장", "관악신사시장", "이마트 이수점", "롯데백화점 영등포점")
            martData["관악구"] = arrayListOf("신원시장", "원당종합시장", "롯데백화점 관악점", "세이브마트 신림본점")
            martData["서초구"] = arrayListOf("방배종합시장", "신세계백화점 강남점", "뉴코아아울렛 강남점", "농협하나로마트 양재점")
            martData["강남구"] = arrayListOf("청담삼익시장", "도곡시장", "롯데백화점 강남점", "이마트 역삼점")
            martData["송파구"] = arrayListOf("방이시장", "마천중앙시장", "홈플러스 잠실점", "롯데백화점 잠실점")
            martData["강동구"] = arrayListOf("둔촌역전통시장", "암사종합시장", "홈플러스 강동점", "이마트 명일점")
            return martData
        }

        fun getMart(area: String) = martData[area]
        fun getGuList() = martData.values
        fun setMartPoiList(): HashMap<String, MapPoint> {
            val martList = HashMap<String, MapPoint>()
            martList["광장시장"] = MapPoint.mapPointWithGeoCoord(37.5700282666334, 126.998949546525)
            martList["통인시장"] = MapPoint.mapPointWithGeoCoord(37.5807751333021, 126.970026074345)
            martList["롯데백화점 명동본점"] =
                MapPoint.mapPointWithGeoCoord(37.564905601247865, 126.98175414832635)
            martList["신세계백화점 본점"] =
                MapPoint.mapPointWithGeoCoord(37.560948320099605, 126.98108960513339)
            martList["서울중앙시장"] =
                MapPoint.mapPointWithGeoCoord(37.56737408602461, 127.01976321851687)
            martList["남대문시장"] = MapPoint.mapPointWithGeoCoord(37.55918176072071, 126.9776267740439)
            martList["이마트 청계천점"] =
                MapPoint.mapPointWithGeoCoord(37.57134801872825, 127.02179955803058)
            martList["롯데마트 서울역점"] =
                MapPoint.mapPointWithGeoCoord(37.55600861253633, 126.97021254886772)
            martList["후암시장"] = MapPoint.mapPointWithGeoCoord(37.55029311763281, 126.97609377452645)
            martList["용문시장"] = MapPoint.mapPointWithGeoCoord(37.5366232338992, 126.959792827553)
            martList["농협하나로마트 용산점"] =
                MapPoint.mapPointWithGeoCoord(37.5332262104177, 126.964641558868)
            martList["이마트 용산점"] =
                MapPoint.mapPointWithGeoCoord(37.52968200913392, 126.96578815221102)
            martList["금남시장"] = MapPoint.mapPointWithGeoCoord(37.54845261293641, 127.02271403358449)
            martList["뚝도시장"] = MapPoint.mapPointWithGeoCoord(37.53779946404449, 127.05482635396496)
            martList["이마트 성수점"] =
                MapPoint.mapPointWithGeoCoord(37.54013560882059, 127.05318059759344)
            martList["이마트 왕십리점"] = MapPoint.mapPointWithGeoCoord(37.561703921452, 127.038735688303)
            martList["노룬산골목시장"] = MapPoint.mapPointWithGeoCoord(37.5365905315237, 127.065062813901)
            martList["자양골목시장"] = MapPoint.mapPointWithGeoCoord(37.5348808261609, 127.079172429807)
            martList["롯데마트 강변점"] =
                MapPoint.mapPointWithGeoCoord(37.53586466606496, 127.09622875485857)
            martList["이마트 자양점"] =
                MapPoint.mapPointWithGeoCoord(37.53841287581957, 127.07339432262434)
            martList["경동시장"] = MapPoint.mapPointWithGeoCoord(37.57903713704342, 127.03913636936471)
            martList["청량리종합시장"] = MapPoint.mapPointWithGeoCoord(37.5794673017256, 127.040646811902)
            martList["롯데백화점 청량리점"] =
                MapPoint.mapPointWithGeoCoord(37.58082967458468, 127.0476836634231)
            martList["홈플러스 동대문점"] =
                MapPoint.mapPointWithGeoCoord(37.5746349983527, 127.038774081903)
            martList["동원시장"] = MapPoint.mapPointWithGeoCoord(37.589796969633504, 127.08991450537886)
            martList["우림시장"] = MapPoint.mapPointWithGeoCoord(37.5970099681642, 127.098252841871)
            martList["홈플러스 면목점"] = MapPoint.mapPointWithGeoCoord(37.5805270297788, 127.081974038406)
            martList["이마트 상봉점"] = MapPoint.mapPointWithGeoCoord(37.5964371115971, 127.093604909066)
            martList["장위전통시장"] =
                MapPoint.mapPointWithGeoCoord(37.61031850457807, 127.05130856928078)
            martList["돈암제일시장"] = MapPoint.mapPointWithGeoCoord(37.5917555562937, 127.015969721794)
            martList["현대백화점 미아점"] =
                MapPoint.mapPointWithGeoCoord(37.608482732465, 127.028748311194)
            martList["이마트 미아점"] = MapPoint.mapPointWithGeoCoord(37.6108502600034, 127.029845552727)
            martList["숭인시장"] = MapPoint.mapPointWithGeoCoord(37.613315546618, 127.029121667591)
            martList["수유재래시장"] =
                MapPoint.mapPointWithGeoCoord(37.63154191894609, 127.02376795939124)
            martList["농협하나로마트 미아점"] =
                MapPoint.mapPointWithGeoCoord(37.6215475015042, 127.026900187176)
            martList["롯데백화점 미아점"] =
                MapPoint.mapPointWithGeoCoord(37.61487570735506, 127.03051315008364)
            martList["신창시장"] = MapPoint.mapPointWithGeoCoord(37.6394346028627, 127.03745719002)
            martList["방학동 도깨비시장"] =
                MapPoint.mapPointWithGeoCoord(37.6653583281858, 127.035353021784)
            martList["이마트 창동점"] = MapPoint.mapPointWithGeoCoord(37.6517925939067, 127.047226995957)
            martList["홈플러스 방학점"] = MapPoint.mapPointWithGeoCoord(37.6648473900421, 127.043667346598)
            martList["상계중앙시장"] = MapPoint.mapPointWithGeoCoord(37.6593204454215, 127.070423786295)
            martList["공릉동 도깨비시장"] =
                MapPoint.mapPointWithGeoCoord(37.6228516123482, 127.077131523386)
            martList["홈플러스 중계점"] = MapPoint.mapPointWithGeoCoord(37.6400836787935, 127.068687971734)
            martList["롯데백화점 노원점"] =
                MapPoint.mapPointWithGeoCoord(37.65507468704351, 127.06133796050443)
            martList["대조시장"] = MapPoint.mapPointWithGeoCoord(37.6098480496794, 126.927770024237)
            martList["대림시장"] = MapPoint.mapPointWithGeoCoord(37.5868085534625, 126.917738285979)
            martList["이마트 은평점"] = MapPoint.mapPointWithGeoCoord(37.6002746471855, 126.920178530965)
            martList["NC백화점 불광점"] =
                MapPoint.mapPointWithGeoCoord(37.60970729312205, 126.9289208362778)
            martList["독립문영천시장"] =
                MapPoint.mapPointWithGeoCoord(37.56970341646108, 126.96266831410814)
            martList["인왕시장"] = MapPoint.mapPointWithGeoCoord(37.59093296706887, 126.94317699687126)
            martList["롯데슈퍼 유진점"] =
                MapPoint.mapPointWithGeoCoord(37.59131440700395, 126.94198328760322)
            martList["현대백화점 신촌점"] =
                MapPoint.mapPointWithGeoCoord(37.556153500544426, 126.93581652251622)
            martList["망원시장"] = MapPoint.mapPointWithGeoCoord(37.5553049791046, 126.90646467103)
            martList["마포농수산물시장"] =
                MapPoint.mapPointWithGeoCoord(37.5648030101604, 126.89849571788136)
            martList["농협하나로마트 신촌점"] =
                MapPoint.mapPointWithGeoCoord(37.556184385050464, 126.93302335049411)
            martList["홈플러스 월드컵점"] =
                MapPoint.mapPointWithGeoCoord(37.568323281381566, 126.89859055072421)
            martList["신영시장"] = MapPoint.mapPointWithGeoCoord(37.5330239610442, 126.836024765935)
            martList["목3동시장"] = MapPoint.mapPointWithGeoCoord(37.5482427777087, 126.866853074284)
            martList["홈플러스 목동점"] = MapPoint.mapPointWithGeoCoord(37.5302586999185, 126.873288502027)
            martList["이마트 목동점"] =
                MapPoint.mapPointWithGeoCoord(37.52556123754674, 126.8702985523677)
            martList["화곡본동시장"] = MapPoint.mapPointWithGeoCoord(37.5432550860872, 126.843533944782)
            martList["송화시장"] = MapPoint.mapPointWithGeoCoord(37.5493927928677, 126.835653948299)
            martList["홈플러스 익스프레스 등촌점"] =
                MapPoint.mapPointWithGeoCoord(37.5602011098443, 126.846328025754)
            martList["남구로시장"] = MapPoint.mapPointWithGeoCoord(37.48990787918405, 126.88726031443463)
            martList["고척근린시장"] = MapPoint.mapPointWithGeoCoord(37.50213699646185, 126.8498025691697)
            martList["이마트 신도림점"] = MapPoint.mapPointWithGeoCoord(37.5067164863743, 126.890496837047)
            martList["NC백화점 신구로점"] =
                MapPoint.mapPointWithGeoCoord(37.50114804483891, 126.88274690675226)
            martList["별빛남문시장"] =
                MapPoint.mapPointWithGeoCoord(37.47376642745937, 126.90042549990117)
            martList["현대시장"] = MapPoint.mapPointWithGeoCoord(37.4550045698301, 126.90137941780802)
            martList["홈플러스 독산점"] = MapPoint.mapPointWithGeoCoord(37.4686781664851, 126.896973152729)
            martList["홈플러스 시흥점"] =
                MapPoint.mapPointWithGeoCoord(37.45207769375974, 126.90091762899948)
            martList["영등포 전통시장"] =
                MapPoint.mapPointWithGeoCoord(37.519851893042606, 126.90807791194665)
            martList["대림중앙시장"] =
                MapPoint.mapPointWithGeoCoord(37.491297483226475, 126.89952023594192)
            martList["이마트 여의도점"] =
                MapPoint.mapPointWithGeoCoord(37.51831167296785, 126.92683576753619)
            martList["홈플러스 영등포점"] =
                MapPoint.mapPointWithGeoCoord(37.5182397970571, 126.895891522092)
            martList["남성시장"] = MapPoint.mapPointWithGeoCoord(37.48834166282217, 126.97938922792724)
            martList["관악신사시장"] = MapPoint.mapPointWithGeoCoord(37.4863536067363, 126.916849029998)
            martList["이마트 이수점"] =
                MapPoint.mapPointWithGeoCoord(37.484554065992896, 126.98082394956725)
            martList["롯데백화점 영등포점"] =
                MapPoint.mapPointWithGeoCoord(37.5160148076443, 126.907291953523)
            martList["신원시장"] = MapPoint.mapPointWithGeoCoord(37.4825831435359, 126.926766602033)
            martList["원당종합시장"] = MapPoint.mapPointWithGeoCoord(37.474405334586, 126.965890239084)
            martList["롯데백화점 관악점"] =
                MapPoint.mapPointWithGeoCoord(37.490440600103085, 126.92495764390794)
            martList["세이브마트 신림본점"] =
                MapPoint.mapPointWithGeoCoord(37.4742735438981, 126.917798475541)
            martList["방배종합시장"] = MapPoint.mapPointWithGeoCoord(37.4819631963305, 126.983696397436)
            martList["신세계백화점 강남점"] =
                MapPoint.mapPointWithGeoCoord(37.505305732081965, 127.00432032924516)
            martList["뉴코아아울렛 강남점"] =
                MapPoint.mapPointWithGeoCoord(37.5092141277711, 127.00757793588681)
            martList["농협하나로마트 양재점"] =
                MapPoint.mapPointWithGeoCoord(37.46359008942609, 127.04358620220383)
            martList["청담삼익시장"] =
                MapPoint.mapPointWithGeoCoord(37.52247747058278, 127.05769300261032)
            martList["도곡시장"] = MapPoint.mapPointWithGeoCoord(37.4974897969693, 127.05212804503)
            martList["롯데백화점 강남점"] =
                MapPoint.mapPointWithGeoCoord(37.496959490738, 127.053281143657)
            martList["이마트 역삼점"] = MapPoint.mapPointWithGeoCoord(37.49930420889, 127.048397383795)
            martList["방이시장"] = MapPoint.mapPointWithGeoCoord(37.5117726264435, 127.113632180173)
            martList["마천중앙시장"] = MapPoint.mapPointWithGeoCoord(37.4981659784072, 127.150423566591)
            martList["홈플러스 잠실점"] = MapPoint.mapPointWithGeoCoord(37.5162837846713, 127.103006291115)
            martList["롯데백화점 잠실점"] =
                MapPoint.mapPointWithGeoCoord(37.51201141930842, 127.0985552869803)
            martList["둔촌역전통시장"] = MapPoint.mapPointWithGeoCoord(37.5274056755862, 127.135245792615)
            martList["암사종합시장"] = MapPoint.mapPointWithGeoCoord(37.5508604719985, 127.128819574465)
            martList["홈플러스 강동점"] =
                MapPoint.mapPointWithGeoCoord(37.54555802503742, 127.14222876383826)
            martList["이마트 명일점"] = MapPoint.mapPointWithGeoCoord(37.5546046874526, 127.155987297642)
            return martList
        }

        var martList = arrayListOf(
            MartRow("현대시장", "전통시장"),
            MartRow("별빛남문시장", "전통시장"),
            MartRow("홈플러스 독산점", "대형마트"),
            MartRow("홈플러스 시흥점", "대형마트")
        )
    }
}