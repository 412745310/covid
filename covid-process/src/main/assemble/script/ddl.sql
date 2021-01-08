-- 疫情物资表
CREATE TABLE `covid_wz` (
  `name` varchar(100) NOT NULL COMMENT '物资名称',
  `caigou` int(11) DEFAULT NULL COMMENT '采购',
  `xiabo` int(11) DEFAULT NULL COMMENT '下拨',
  `juanzeng` int(11) DEFAULT NULL COMMENT '捐赠',
  `xiaohao` int(11) DEFAULT NULL COMMENT '消耗',
  `xuqiu` int(11) DEFAULT NULL COMMENT '需求',
  `kucun` int(11) DEFAULT NULL COMMENT '库存',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8