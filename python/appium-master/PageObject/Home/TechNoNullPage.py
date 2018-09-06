from PageObject import Pages


class TechNotNullPage:
    '''
    技术专区卡片下数据不为空
    isOperate: 操作失败，检查点就失败,kwargs: WebDriver driver, String path(yaml配置参数)
    '''

    def __init__(self, **kwargs):
        self.driver = kwargs["driver"]
        self.path = kwargs["path"]
        self.page = Pages.PagesObjects(driver=self.driver, path=kwargs["path"])

    def operate(self, logTest):  # 操作步骤
        self.page.operate(logTest)

    def checkPoint(self, caseName, logTest, devices):  # 检查点
        self.page.checkPoint(caseName=caseName, logTest=logTest, devices=devices)


if __name__ == "__main__":
    pass
