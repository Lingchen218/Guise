这个软件从第一个版本发布到现在应该有一年多了，具体哪天发布的我已经忘记了，也懒得去看。

只记得在某天晚上下载了王者荣耀发现其并没有开发我当时使用的设备的120帧开关，于是我想要去改机型，当时应用变量和应用伪装最后一个免费版本在我的设备上已经无法使用。我去了解了源计划，但是那个时候我还是大学生，50块钱的永久会员对于那时候的我有点肉疼，于是我便趁着寒假自己开发了一个类似的软件，后面开学后就一直没有更新。至于为什么最近更新了，有点难评…… 我马上毕业了，我在学校主修的方向是Java后端开发，但是无奈一直找不到工作。其实这次更新是想加入一些付费内容，可以看到最新的alpha版本移除了内置的机型库，我其实是想从这方面收费的，基础功能免费且继续开源。这几天也想了很多，这个APP诞生的初衷就是为了免费，我并不想违背我的初衷，而且这个APP并没有什么技术含量，属于非常简单的，任何一个计算机专业的大一学生可以做出来一个类似的。

说了这么多也不知道说了说什么，还是说说重点吧，这个APP我已经决定停更,感兴趣的可以接手继续开发。感谢这段时间大家的支持。

lint-baseline.xml 文件
Rls编译如果遇到报错 请查看这个文件

错误提示
Missing classes detected while running R8. Please add the missing classes or apply additional keep rules that are generated in D:\Guise\app\build\outputs\mapping\release\missing_rules.txt

解决方法 把 missing_rules.txt 内的类 复制到 
build.gradle.kts 中 getDefaultProguardFile 中配置的 proguard-rules.pro文件内
