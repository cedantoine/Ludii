package view.component.custom.types;

/**
 * Shogi types.
 *
 * @author Matthew.Stephenson and Eric Piette
 */
public enum ShogiType
{
	KING("玉将", "gyokushō", "King"),
	PRINCE("太子", "taishi", "Prince"),
	GOLDGENERAL("金将", "kinshō", "Gold general"),
	RIGHTGENERAL("右将", "ushō", "Right general"),
	RIGHTARMY("右軍", "ugun", "Right army"),
	LEFTGENERAL("左将", "sashō", "Left general"),
	LEFTARMY("左軍", "sagun", "Left army"),
	REARSTANDARD("後旗", "kōki", "Rear standard"),
	QUEEN("奔王", "honnō", "Queen"),
	FREEDREAMEATER("奔獏", "honbaku", "Free dream-eater"),
	WOODENDOVE("鳩槃", "kyūhan", "Wooden dove"),
	CERAMICDOVE("鳩盤", "kyūban", "Ceramic dove"),
	EARTHDRAGON("地龍", "chiryū", "Earth dragon"),
	FREEDEMON("奔鬼", "honki", "Free demon"),
	RUNNINGHORSE("走馬", "sōma", "Running horse"),
	BEASTCADET("獣曹", "jūsō", "Beast cadet"),
	LONGNOSEDGOBLIN("天狗", "tengu", "Long-nosed goblin"),
	MOUNTAINEAGLELEFT("山鷲鷲", "sanshū left", "Mountain eagle left"),
	MOUNTAINEAGLERIGHT("山鷲山", "sanshū right", "Mountain eagle right"),
	FIREDEMON("火鬼", "kaki", "Fire demon"),
	FREEFIRE("奔火", "honka", "Free fire"),
	WHALE("鯨鯢", "keigei", "Whale"),
	GREATWHALE("大鯨", "daigei", "Great whale"),
	RUNNINGRABBIT("走兎", "sōto", "Running rabbit"),
	WHITETIGER("白虎", "byakko", "White tiger"),
	DIVINETIGER("神虎", "shinko", "Divine tiger"),
	TURTLESNAKE("玄武", "genbu", "Turtle-snake"),
	DIVINETURTLE("神亀", "shinki", "Divine turtle"),
	LANCE("香車", "kyōsha", "Lance"),
	REVERSECHARIOT("反車", "hensha", "Reverse chariot"),
	FRAGRANTELEPHANT("香象", "kōzō", "Fragrant elephant"),
	ELEPHANTKING("象王", "zōō", "Elephant king"),
	WHITEELEPHANT("白象", "hakuzō", "White elephant"),
	TURTLEDOVE("山鳩", "sankyū", "Turtle dove"),
	FLYINGSWALLOW("飛燕", "hien", "Flying swallow"),
	CAPTIVEOFFICER("禽吏", "kinri", "Captive officer"),
	CAPTIVEBIRD("禽鳥", "kinchō", "Captive bird"),
	RAINDRAGON("雨龍", "uryū", "Rain dragon"),
	FORESTDEMON("森鬼", "shinki", "Forest demon"),
	THUNDERRUNNER("雷走", "raisō", "Thunder runner"),
	MOUNTAINSTAG("山鹿", "sanroku", "Mountain stag"),
	RUNNINGPUP("走狗", "sōku", "Running pup"),
	FREELEOPARD("奔豹", "honpyō", "Free leopard"),
	RUNNINGSERPENT("走蛇", "sōja", "Running serpent"),
	FREESERPENT("奔蛇", "honja", "Free serpent"),
	SIDESERPENT("横蛇", "ōja", "Side serpent"),
	GREATSHARK("大鱗", "dairin", "Great shark"),
	GREATDOVE("大鳩", "daikyū", "Great dove"),
	RUNNINGTIGER("走虎", "sōko", "Running tiger"),
	FREETIGER("奔虎", "honko", "Free tiger"),
	RUNNINGBEAR("走熊", "sōyū", "Running bear"),
	FREEBEAR("奔熊", "hon’yū", "Free bear"),
	YAKSHA("夜叉", "yasha", "Yaksha"),
	HEAVENLYTETRARCH("四天", "shiten", "Heavenly Tetrarch"),
	BUDDHISTDEVIL("羅刹", "rasetsu", "Buddhist devil"),
	GUARDIANOFTHEGODS("金剛", "kongō", "Guardian of the Gods"),
	WRESTLER("力士", "rikishi", "Wrestler"),
	SILVERGENERAL("銀将", "ginshō", "Silver general"),
	DRUNKENELEPHANT("酔象", "suizō", "Drunken elephant"),
	NEIGHBORINGKING("近王", "kinnō", "Neighboring king"),
	GOLDCHARIOT("金車", "kinsha", "Gold chariot"),
	PLAYFULCOCKATOO("遊母", "yūmo", "Playful cockatoo"),
	SIDEDRAGON("横龍", "ōryū", "Side dragon"),
	RUNNINGDRAGON("走龍", "sōryū", "Running dragon"),
	RUNNINGSTAG("走鹿", "sōroku", "Running stag"),
	FREESTAG("奔鹿", "honroku", "Free stag"),
	RUNNINGWOLF("走狼", "sōrō", "Running wolf"),
	FREEWOLF("奔狼", "honrō", "Free wolf"),
	BISHOPGENERAL("角将", "kakushō", "Bishop general"),
	RAINDEMON("霖鬼", "rinki", "Rain demon"),
	ROOKGENERAL("飛将", "hishō", "Rook general"),
	FLYINGCROCODILE("飛鰐", "higaku", "Flying crocodile"),
	RIGHTTIGER("右虎", "uko", "Right tiger"),
	LEFTTIGER("左虎", "sako", "Left tiger"),
	RIGHTDRAGON("右龍", "uryū", "Right dragon"),
	LEFTDRAGON("左龍", "saryū", "Left dragon"),
	BEASTOFFICER("獣吏", "jūri", "Beast officer"),
	BEASTBIRD("獣鳥", "jūchō", "Beast bird"),
	WINDDRAGON("風龍", "fūryū", "Wind dragon"),
	FREEDRAGON("奔龍", "honryū", "Free dragon"),
	FREEPUP("奔狗", "honku", "Free pup"),
	FREEDOG("奔犬", "honken", "Free dog"),
	RUSHINGBIRD("行鳥", "gyōchō", "Rushing bird"),
	OLDKITE("古鵄", "kotetsu", "Old kite"),
	PEACOCK("孔雀", "kujaku", "Peacock"),
	WATERDRAGON("水龍", "suiryū", "Water dragon"),
	FIREDRAGON("火龍", "karyū", "Fire dragon"),
	COPPERGENERAL("銅将", "dōshō", "Copper general"),
	PHOENIXMASTER("鳳師", "hōshi", "Phoenix master"),
	KIRINMASTER("麟師", "rinshi", "Kirin master"),
	SILVERCHARIOT("銀車", "ginsha", "Silver chariot"),
	GOOSEWING("鴻翼", "kōyoko", "Goose wing"),
	VERTICALBEAR("竪熊", "shuyū", "Vertical bear"),
	KNIGHT("桂馬", "keima", "Knight"),
	PIGGENERAL("豚将", "tonshō", "Pig general"),
	FREEPIG("奔豚", "honton", "Free pig"),
	CHICKENGENERAL("鶏将", "keishō", "Chicken general"),
	FREECHICKEN("奔鶏", "honkei", "Free chicken"),
	PUPGENERAL("狗将", "kushō", "Pup general"),
	HORSEGENERAL("馬将", "bashō", "Horse general"),
	FREEHORSE("奔馬", "honba", "Free horse"),
	OXGENERAL("牛将", "gyūshō", "Ox general"),
	FREEOX("奔牛", "hongyū", "Free ox"),
	CENTERSTANDARD("中旗", "chūki", "Center standard"),
	SIDEBOAR("横猪", "ōcho", "Side boar"),
	FREEBOAR("奔猪", "honcho", "Free boar"),
	SILVERRABBIT("銀兎", "ginto", "Silver rabbit"),
	GOLDENDEER("金鹿", "konroku", "Golden deer"),
	LION("獅子", "shishi", "Lion"),
	FURIOUSFIEND("奮迅", "funjin", "Furious fiend"),
	CAPTIVECADET("禽曹", "kinsō", "Captive cadet"),
	GREATSTAG("大鹿", "dairoku", "Great stag"),
	VIOLENTDRAGON("猛龍", "mōryū", "Violent dragon"),
	WOODLANDDEMON("林鬼", "rinki", "Woodland demon"),
	RIGHTPHOENIX("右鵰", "ushū", "Right phoenix"),
	VICEGENERAL("副将", "fukushō", "Vice general"),
	GREATGENERAL("大将", "taishō", "Great general"),
	STONECHARIOT("石車", "sekisha", "Stone chariot"),
	WALKINGHERON("歩振", "fushin", "Walking heron"),
	CLOUDEAGLE("雲鷲", "unjū", "Cloud eagle"),
	STRONGEAGLE("勁鷲", "keijū", "Strong eagle"),
	BISHOP("角行", "kakugyō", "Bishop"),
	ROOK("飛車", "hisha", "Rook"),
	SIDEWOLF("横狼", "ōrō", "Side wolf"),
	FLYINGCAT("飛猫", "hibyō", "Flying cat"),
	MOUNTAINFALCON("山鷹", "san’ō", "Mountain falcon"),
	VERTICALTIGER("竪虎", "shuko", "Vertical tiger"),
	SOLDIER("兵士", "heishi", "Soldier"),
	CAVALIER("騎士", "kishi", "Cavalier"),
	LITTLESTANDARD("小旗", "shōki", "Little standard"),
	CLOUDDRAGON("雲龍", "unryū", "Cloud dragon"),
	COPPERCHARIOT("銅車", "dōsha", "Copper chariot"),
	COPPERELEPHANT("銅象", "dōzō", "Copper elephant"),
	RUNNINGCHARIOT("走車", "sōsha", "Running chariot"),
	BURNINGCHARIOT("炮車", "hōsha", "Burning chariot"),
	RAMSHEADSOLDIER("羊兵", "yōhei", "Ram's-head soldier"),
	TIGERSOLDIER("虎兵", "kohei", "Tiger soldier"),
	VIOLENTOX("猛牛", "mōgyū", "Violent ox"),
	GREATDRAGON("大龍", "dairyū", "Great dragon"),
	ANCIENTDRAGON("元龍", "genryū, ganryū", "Ancient dragon"),
	GOLDENBIRD("金翅", "kinshi", "Golden bird"),
	FREEBIRD("奔翅", "honshi", "Free bird"),
	DARKSPIRIT("無明", "mumyō", "Dark spirit"),
	BUDDHISTSPIRIT("法性", "hōsei", "Buddhist spirit"),
	DEVA("提婆", "daiba", "Deva"),
	TEACHINGKING("教王", "kyōō", "Teaching king"),
	WOODCHARIOT("木車", "mokusha", "Wood chariot"),
	WINDSNAPPINGTURTLE("風鼈", "fūbetsu", "Wind snapping turtle"),
	WHITEHORSE("白駒", "hakku", "White horse"),
	GREATHORSE("大駒", "daiku", "Great horse"),
	HOWLINGDOG("駒犬", "kiken", "Howling dog"),							//symbol not correct, but won't render otherwise
	RIGHTDOG("右犬", "uken", "Right dog"),
	LEFTDOG("左犬", "saken", "Left dog"),
	SIDEMOVER("横行", "ōgyō", "Side mover"),
	PRANCINGSTAG("踊鹿", "yōroku", "Prancing stag"),
	WATERBUFFALO("水牛", "suigyū", "Water buffalo"),
	GREATDREAMEATER("大獏", "daibaku", "Great dream-eater"),
	FEROCIOUSLEOPARD("猛豹", "mōhyō", "Ferocious leopard"),
	FIERCEEAGLE("猛鷲", "mōjū", "Fierce eagle"),
	FLYINGDRAGON("飛龍", "hiryū", "Flying dragon"),
	POISONOUSSNAKE("毒蛇", "dokuja", "Poisonous snake"),
	FLYINGGOOSE("鳫飛", "ganhi", "Flying goose"),
	STRUTTINGCROW("烏行", "ukō", "Strutting crow"),
	FLYINGFALCON("飛鷹", "hiyō", "Flying falcon"),
	BLINDDOG("盲犬", "mōken", "Blind dog"),
	WATERGENERAL("水将", "suishō", "Water general"),
	FIREGENERAL("火将", "kashō", "Fire general"),
	PHOENIX("鳳凰", "hōō", "Phoenix"),
	KIRIN("麒麟", "kirin", "Kirin"),
	HOOKMOVER("鉤行", "kōgyō", "Hook mover"),
	LITTLETURTLE("小亀", "shōki", "Little turtle"),
	TREASURETURTLE("宝亀", "hōki", "Treasure turtle"),
	GREATTURTLE("大亀", "daiki", "Great turtle"),
	SPIRITTURTLE("霊亀", "reiki", "Spirit turtle"),
	CAPRICORN("摩羯", "makatsu", "Capricorn"),
	TILECHARIOT("瓦車", "gasha", "Tile chariot"),
	RUNNINGTILE("走瓦", "sōga", "Running tile"),
	VERTICALWOLF("竪狼", "shurō", "Vertical wolf"),
	SIDEOX("横牛", "ōgyū", "Side ox"),
	DONKEY("驢馬", "roba", "Donkey"),
	FLYINGHORSE("馬麟", "barin", "Flying horse"),
	VIOLENTBEAR("猛熊", "mōyū", "Violent bear"),
	GREATBEAR("大熊", "daiyū", "Great bear"),
	ANGRYBOAR("嗔猪", "shincho", "Angry boar"),
	EVILWOLF("悪狼", "akurō", "Evil wolf"),
	VENOMOUSWOLF("毒狼", "dokurō", "Venomous wolf"),
	LIBERATEDHORSE("風馬", "fūma", "Liberated horse"),
	HEAVENLYHORSE("天馬", "temma", "Heavenly horse"),
	FLYINGCOCK("鶏飛", "keihi", "Flying cock"),
	RAIDINGFALCON("延鷹", "en’yō", "Raiding falcon"),
	OLDMONKEY("古猿", "koen", "Old monkey"),
	MOUNTAINWITCH("山母", "sanbo", "Mountain witch"),
	CHINESECOCK("淮鶏", "waikei", "Chinese cock"),
	WIZARDSTORK("仙鷦", "senkaku", "Wizard stork"),
	NORTHERNBARBARIAN("北狄", "hokuteki", "Northern barbarian"),
	SOUTHERNBARBARIAN("南蛮", "nanban", "Southern barbarian"),
	WESTERNBARBARIAN("西戎", "seijū", "Western barbarian"),
	EASTERNBARBARIAN("東夷", "tōi", "Eastern barbarian"),
	VIOLENTSTAG("猛鹿", "mōroku", "Violent stag"),
	RUSHINGBOAR("行猪", "gyōcho", "Rushing boar"),
	VIOLENTWOLF("猛狼", "mōrō", "Violent wolf"),
	BEARSEYES("熊眼", "yūgan", "Bear's eyes"),
	TREACHEROUSFOX("隠狐", "inko, onko", "Treacherous fox"),
	MOUNTAINCRANE("山鶻", "sankotsu", "Mountain crane"),
	CENTERMASTER("中師", "chūshi", "Center master"),
	ROCMASTER("鵬師", "hōshi", "Roc master"),
	EARTHCHARIOT("土車", "dosha", "Earth chariot"),
	YOUNGBIRD("𦬨鳥", "shakuchō", "Young bird"),
	VERMILLIONSPARROW("朱雀", "suzaku", "Vermillion sparrow"),
	DIVINESPARROW("神雀", "shinjaku", "Divine sparrow"),
	BLUEDRAGON("青龍", "seiryū", "Blue dragon"),
	DIVINEDRAGON("神龍", "shinryū", "Divine dragon"),
	ENCHANTEDBADGER("変狸", "henri", "Enchanted badger"),
	HORSEMAN("騎兵", "kihei", "Horseman"),
	SWOOPINGOWL("鴟行", "shigyō", "Swooping owl"),
	CLIMBINGMONKEY("登猿", "tōen", "Climbing monkey"),
	CATSWORD("猫刄", "myōjin", "Cat sword"),
	SWALLOWSWINGS("燕羽", "en’u", "Swallow's wings"),
	GLIDINGSWALLOW("燕行", "engyō", "Gliding swallow"),
	BLINDMONKEY("盲猿", "mōen", "Blind monkey"),
	FLYINGSTAG("飛鹿", "hiroku", "Flying stag"),
	BLINDTIGER("盲虎", "mōko", "Blind tiger"),
	OXCART("牛車", "gissha", "Oxcart"),
	PLODDINGOX("歬牛", "sengyū", "Plodding ox"),
	SIDEFLIER("横飛", "ōhi", "Side flier"),
	BLINDBEAR("盲熊", "mōyū", "Blind bear"),
	OLDRAT("老鼠", "rōso", "Old rat"),
	BIRDOFPARADISE("古寺", "jichō", "Bird of paradise"),
	SQUAREMOVER("方行", "hōgyō", "Square mover"),
	STRONGCHARIOT("強車", "kyōsha", "Strong chariot"),
	COILEDSERPENT("蟠蛇", "banja", "Coiled serpent"),
	COILEDDRAGON("蟠龍", "banryū", "Coiled dragon"),
	RECLININGDRAGON("臥龍", "garyū", "Reclining dragon"),
	FREEEAGLE("奔鷲", "honjū", "Free eagle"),
	LIONHAWK("獅鷹", "shiō", "Lion hawk"),
	CHARIOTSOLDIER("車兵", "shahei", "Chariot soldier"),
	HEAVENLYTETRARCHKING("四天王", "shitennō", "Heavenly Tetrarch king"),
	SIDESOLDIER("横兵", "ōhei", "Side soldier"),
	VERTICALSOLDIER("竪兵", "shuhei", "Vertical soldier"),
	WINDGENERAL("風将", "fūshō", "Wind general"),
	VIOLENTWIND("暴風", "bōfū", "Violent wind"),
	RIVERGENERAL("川将", "senshō", "River general"),
	CHINESERIVER("淮川", "waisen", "Chinese river"),
	MOUNTAINGENERAL("山将", "sanshō", "Mountain general"),
	PEACEFULMOUNTAIN("泰山", "taizan", "Peaceful mountain"),
	FRONTSTANDARD("前旗", "zenki", "Front standard"),
	HORSESOLDIER("馬兵", "bahei", "Horse soldier"),
	WOODGENERAL("木将", "mokushō", "Wood general"),
	OXSOLDIER("牛兵", "gyūhei", "Ox soldier"),
	RUNNINGOX("走牛", "sōgyū", "Running ox"),
	EARTHGENERAL("土将", "doshō", "Earth general"),
	BOARSOLDIER("猪兵", "chohei", "Boar soldier"),
	RUNNINGBOAR("走猪", "sōcho", "Running boar"),
	STONEGENERAL("石将", "sekishō", "Stone general"),
	LEOPARDSOLDIER("豹兵", "hyōhei", "Leopard soldier"),
	RUNNINGLEOPARD("走豹", "sōhyō", "Running leopard"),
	TILEGENERAL("瓦将", "gashō", "Tile general"),
	BEARSOLDIER("熊兵", "yūhei", "Bear soldier"),
	STRONGBEAR("強熊", "kyōyū", "Strong bear"),
	IRONGENERAL("鉄将", "tesshō", "Iron general"),
	GREATSTANDARD("大旗", "daiki", "Great standard"),
	GREATMASTER("大師", "daishi", "Great master"),
	RIGHTCHARIOT("右車", "usha", "Right chariot"),
	RIGHTIRONCHARIOT("右鉄車", "utessha", "Right iron chariot"),
	LEFTCHARIOT("左車", "sasha", "Left chariot"),
	LEFTIRONCHARIOT("左鉄車", "satessha", "Left iron chariot"),
	SIDEMONKEY("横猿", "ōen", "Side monkey"),
	VERTICALMOVER("竪行", "shugyō", "Vertical mover"),
	FLYINGOX("飛牛", "higyū", "Flying ox"),
	FIREOX("火牛", "kagyū", "Fire ox"),
	LONGBOWSOLDIER("弩兵", "dohei", "Longbow soldier"),
	LONGBOWGENERAL("弩将", "doshō", "Longbow general"),
	VERTICALPUP("竪狗", "shuku", "Vertical pup"),
	LEOPARDKING("豹王", "hyōō", "Leopard king"),
	VERTICALHORSE("竪馬", "shuba", "Vertical horse"),
	BURNINGSOLDIER("炮兵", "hōhei", "Burning soldier"),
	BURNINGGENERAL("炮将", "hōshō", "Burning general"),
	DRAGONHORSE("龍馬", "ryūme", "Dragon horse"),
	DRAGONKING("龍王", "ryūō", "Dragon king"),
	SWORDSOLDIER("刀兵", "tōhei", "Sword soldier"),
	SWORDGENERAL("刀将", "tōshō", "Sword general"),
	HORNEDFALCON("角鷹", "kakuō", "Horned falcon"),
	GREATFALCON("大鷹", "daiō", "Great falcon"),
	SOARINGEAGLE("飛鷲", "hijū", "Soaring eagle"),
	GREATEAGLE("大鷲", "daijū", "Great eagle"),
	SPEARSOLDIER("鎗兵", "sōhei", "Spear soldier"),
	SPEARGENERAL("鎗将", "sōshō", "Spear general"),
	VERTICALLEOPARD("竪豹", "shuhyō", "Vertical leopard"),
	GREATLEOPARD("大豹", "daihyō", "Great leopard"),
	SAVAGETIGER("猛虎", "mōko", "Savage tiger"),
	GREATTIGER("大虎", "daiko", "Great tiger"),
	CROSSBOWSOLDIER("弓兵", "kyūhei", "Cross-bow soldier"),
	CROSSBOWGENERAL("弓将", "kyūshō", "Cross-bow general"),
	ROARINGDOG("吼犬", "kōken", "Roaring dog"),
	LIONDOG("狛犬", "komainu", "Lion dog"),
	GREATELEPHANT("大象", "taizō", "Great elephant"),
	DOG("犬", "inu", "Dog"),
	MULTIGENERAL("雜将", "suishō", "Multi general"),
	GOBETWEEN("仲人", "chūnin", "Go between"),
	PAWN("歩兵", "fuhyō", "Pawn"),
	EMPEROR("天王", "tennō", "Emperor"),
	DOVE("鳩槃", "kyūhan", "Dove"),
	POISONSNAKE("毒蛇", "dokuja", "Poison snake"),
	SIDECHARIOT("走車", "sōsha", "Side chariot"),
	SILVERHARE("銀兎", "ginto", "Silver hare"),
	SHEDEVIL("夜叉", "yasha", "She devil"),
	NEIGHBORKING("近王", "kinnō", "Neighbor king"),
	STANDARDBEARER("前旗", "zenki", "Standard bearer"),
	FREEEARTH("奔土", "hondo", "Free earth"),
	FREEGOER("奔人", "honnin", "Free goer"),
	FREESTONE("奔石", "honseki", "Free stone"),
	FREEIRON("奔鉄", "hontetsu", "Free iron"),
	FREETILE("奔瓦", "honga", "Free tile"),
	FREECOPPER("奔銅", "hondō", "Free copper"),
	FREESILVER("奔銀", "hongin", "Free silver"),
	FREEGOLD("奔金", "honkin", "Free gold"),
	FREECAT("奔猫", "honmyō", "Free cat"),
	BAT("蝙蝠", "kōmori", "Bat"),
	CHUNIN("中忍", "Chūnin", "Chunin"),
	TOKIN("と", "Tokin", "Tokin"),
	OSHO("玉将", "Osho", "King"),
	SUIZO("酔象", "suizō", "Drunken elephant"),
	PHENIX("鳳凰", "Hoo", "Phenix"),
	CRANEKING("靏玉", "Kakugyoku", "Crane King"),
	TENACIOUSFALCON("力鷹", "Keiyo", "Tenacious Falcon"),
	SPARROWPAWN("萑歩", "Jakufu", "Sparrow Pawn"),
	RACINGCHARIOT("走車", "Sosha", "Racing Chariot"),
	ENCHANTEDFOX("走車", "Henko", "Enchanted Fox"),
	;

	//-------------------------------------------------------------------------

	private String kanji;
	private String romaji;
	private String englishName;

	//--------------------------------------------------------------------------

	ShogiType(final String kanji, final String romaji, final String englishName)
	{
		this.kanji = kanji;
		this.romaji = romaji;
		this.englishName = englishName;
	}
	
	//-------------------------------------------------------------------------

    public String kanji()
    {
        return kanji;
    }

    public String romaji()
    {
        return romaji;
    }

    public String englishName()
    {
    	return englishName;
    }
}