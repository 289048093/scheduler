项目结构树

┣━assembly #maven打包配置
┣━bin   #项目执行脚本
┣━conf  #项目配置文件
┣━docs  #文档
┣━jobs  #调度任务jar包
┗━src   #code


maven 打包后的目录结构

┣━bin
┣━conf
┣━jobs ┳━ job1 #调度任务1
┃       ┣━  job2 #调度任务2
┃       ... ...
┣━lib   #定时器依赖包
┗━logs  #日志文件
