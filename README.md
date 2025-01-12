# 任务调度中心

### springboot整合quartz的调度中心，实现任务的创建，任务定时执行，日志的记录和查看


For further reference, please consider the following sections:

* 首页
![image](https://github.com/user-attachments/assets/45658c6a-a087-419b-a511-6abbae743cb6)

* 任务列表
![image](https://github.com/user-attachments/assets/d1dc40e1-dbaf-4eeb-a425-42a888e16fed)

* 任务创建
![image](https://github.com/user-attachments/assets/abcad4f3-2b5b-4040-9048-8c128b17e553)

* 日志信息查看
![image](https://github.com/user-attachments/assets/d95f2876-e41a-4c04-aae5-86f56a86dbf4)


### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

