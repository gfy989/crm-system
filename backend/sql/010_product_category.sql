-- 产品分类与产品档案表，表结构来自老师的 009_product_schema.sql，
-- 种子数据换成我们自己的业务：华为各产品线。
-- 执行：mysql -uroot -p123456 --default-character-set=utf8mb4 < sql/010_product_category.sql
USE crm;

CREATE TABLE crm_product_category (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    parent_id BIGINT UNSIGNED NULL COMMENT '上级分类ID，顶级为NULL',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    code VARCHAR(64) NOT NULL COMMENT '分类编码',
    attribute_template JSON NOT NULL DEFAULT (JSON_ARRAY()) COMMENT '本分类差异属性定义数组，不自动继承上级模板',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '同级排序',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '1启用，0停用',
    description VARCHAR(500) NULL COMMENT '分类说明',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_category_code (code),
    KEY idx_product_category_parent_sort (parent_id, sort_order, id),
    CONSTRAINT fk_product_category_parent FOREIGN KEY (parent_id)
        REFERENCES crm_product_category(id) ON DELETE RESTRICT,
    CONSTRAINT chk_product_category_template CHECK (JSON_TYPE(attribute_template) = 'ARRAY'),
    CONSTRAINT chk_product_category_sort CHECK (sort_order >= 0),
    CONSTRAINT chk_product_category_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品分类';

CREATE TABLE crm_product (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '产品ID，一条记录对应一个可售规格SKU',
    category_id BIGINT UNSIGNED NOT NULL COMMENT '所属末级分类ID',
    code VARCHAR(64) NOT NULL COMMENT '产品编码',
    name VARCHAR(150) NOT NULL COMMENT '产品名称',
    specification VARCHAR(255) NOT NULL COMMENT '规格摘要，供列表和单据展示',
    brand VARCHAR(100) NULL COMMENT '品牌',
    material VARCHAR(100) NULL COMMENT '材质，常用筛选字段',
    color VARCHAR(50) NULL COMMENT '颜色，常用筛选字段',
    base_unit VARCHAR(20) NOT NULL COMMENT '基本单位，例如台、个、部',
    reference_price DECIMAL(12,2) NULL COMMENT '人民币含税参考售价，元/基本单位，NULL表示未定价',
    source_type TINYINT UNSIGNED NULL COMMENT '1自制，2外购，NULL待确认',
    attributes JSON NOT NULL DEFAULT (JSON_OBJECT()) COMMENT '分类差异属性，稳定英文编码作为键',
    cover_image VARCHAR(500) NULL COMMENT '主图地址或文件存储路径',
    description TEXT NULL COMMENT '产品说明',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '产品排序',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '1启用，0停用',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (code),
    KEY idx_product_category_status (category_id, status),
    KEY idx_product_status_sort (status, sort_order, id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES crm_product_category(id) ON DELETE RESTRICT,
    CONSTRAINT chk_product_price CHECK (reference_price IS NULL OR reference_price >= 0),
    CONSTRAINT chk_product_source CHECK (source_type IS NULL OR source_type IN (1, 2)),
    CONSTRAINT chk_product_attributes CHECK (JSON_TYPE(attributes) = 'OBJECT'),
    CONSTRAINT chk_product_sort CHECK (sort_order >= 0),
    CONSTRAINT chk_product_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品档案';

-- ============ 种子数据：华为产品线，一级分组 + 二级分类（属性模板放二级） ============
INSERT INTO crm_product_category (id, parent_id, name, code, sort_order, description) VALUES
(1,  NULL, '智能手机',   'PHONE',           1, 'Mate、Pura、nova、畅享四大手机产品线'),
(2,  1,    'Mate 系列',  'PHONE-MATE',      1, '旗舰商务旗舰手机，支持卫星通信'),
(3,  1,    'Pura 系列',  'PHONE-PURA',      2, '影像旗舰手机'),
(4,  1,    'nova 系列',  'PHONE-NOVA',      3, '年轻时尚拍照手机'),
(5,  1,    '畅享系列',   'PHONE-ENJOY',     4, '长续航大屏入门手机'),
(6,  NULL, '笔记本',     'PC',              2, 'MateBook 笔记本产品线'),
(7,  6,    'MateBook X 系列', 'PC-X',       1, '高端轻薄旗舰本'),
(8,  6,    'MateBook D 系列', 'PC-D',       2, '主流性能本'),
(9,  6,    'MateBook E 系列', 'PC-E',       3, '二合一变形本，支持手写笔'),
(10, NULL, '平板',       'TABLET',          3, 'MatePad 平板产品线'),
(11, 10,   'MatePad Pro 系列', 'TAB-PRO',   1, '旗舰生产力平板'),
(12, 10,   'MatePad 系列',     'TAB-MATEPAD', 2, '学习娱乐平板'),
(13, NULL, '智能穿戴',   'WEAR',            4, '手表、手环、耳机、眼镜'),
(14, 13,   'WATCH GT 系列',    'WEAR-WATCHGT', 1, '长续航智能手表'),
(15, 13,   '手环系列',         'WEAR-BAND',    2, '轻便运动手环'),
(16, 13,   'FreeBuds 耳机',    'WEAR-EARBUDS', 3, '真无线蓝牙耳机'),
(17, NULL, '智慧屏',     'VISION',          5, '智慧屏电视产品线'),
(18, 17,   '智慧屏 V 系列',    'VISION-V',     1, '旗舰画质，支持 8K'),
(19, 17,   '智慧屏 S 系列',    'VISION-S',     2, '中端畅连智慧屏'),
(20, NULL, '智能家居',   'HOME',            6, '路由器、音箱、门锁等智选产品'),
(21, 20,   '路由器',           'HOME-ROUTER',  1, 'WiFi 6 / WiFi 7 家用路由器'),
(22, 20,   '智能音箱',         'HOME-SOUND',   2, 'Sound 系列智能音箱'),
(23, 20,   '智能门锁',         'HOME-LOCK',    3, '指纹 / 人脸识别门锁');

-- 二级分类的属性模板：code 用稳定英文编码，material/color 是产品固定字段不允许重复进模板
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','storageGB','name','存储容量','type','enum','options',JSON_ARRAY('256GB','512GB','1TB'),'required',true),
  JSON_OBJECT('code','colorScheme','name','配色','type','enum','options',JSON_ARRAY('雅川青','白沙银','曜金黑'),'required',true),
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',false),
  JSON_OBJECT('code','satellite','name','卫星通信','type','boolean','required',false)
) WHERE id = 2;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','storageGB','name','存储容量','type','enum','options',JSON_ARRAY('256GB','512GB','1TB'),'required',true),
  JSON_OBJECT('code','colorScheme','name','配色','type','enum','options',JSON_ARRAY('拂晓粉','Andesite灰','霜砂白'),'required',true),
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',false)
) WHERE id = 3;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','storageGB','name','存储容量','type','enum','options',JSON_ARRAY('128GB','256GB','512GB'),'required',true),
  JSON_OBJECT('code','colorScheme','name','配色','type','enum','options',JSON_ARRAY('11号色','黑','晨曦金'),'required',true)
) WHERE id = 4;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','storageGB','name','存储容量','type','enum','options',JSON_ARRAY('128GB','256GB'),'required',true),
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',false)
) WHERE id = 5;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','cpuModel','name','处理器','type','string','required',true),
  JSON_OBJECT('code','ramGB','name','内存','type','integer','unit','GB','required',true),
  JSON_OBJECT('code','storageTB','name','固态硬盘','type','number','unit','TB','required',true),
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true),
  JSON_OBJECT('code','weightKg','name','重量','type','number','unit','kg','required',false)
) WHERE id = 7;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','cpuModel','name','处理器','type','string','required',true),
  JSON_OBJECT('code','ramGB','name','内存','type','integer','unit','GB','required',true),
  JSON_OBJECT('code','storageTB','name','固态硬盘','type','number','unit','TB','required',true),
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true)
) WHERE id = 8;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','cpuModel','name','处理器','type','string','required',true),
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true),
  JSON_OBJECT('code','penSupport','name','支持手写笔','type','boolean','required',false)
) WHERE id = 9;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true),
  JSON_OBJECT('code','storageGB','name','存储容量','type','enum','options',JSON_ARRAY('256GB','512GB','1TB'),'required',true),
  JSON_OBJECT('code','keyboardSupport','name','支持键盘','type','boolean','required',false)
) WHERE id = 11;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true),
  JSON_OBJECT('code','storageGB','name','存储容量','type','enum','options',JSON_ARRAY('128GB','256GB'),'required',true)
) WHERE id = 12;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','caseSize','name','表壳尺寸','type','enum','options',JSON_ARRAY('42mm','46mm'),'required',true),
  JSON_OBJECT('code','bandType','name','表带材质','type','enum','options',JSON_ARRAY('氟橡胶','真皮','编织'),'required',false),
  JSON_OBJECT('code','ecgDetect','name','心电分析','type','boolean','required',false)
) WHERE id = 14;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true),
  JSON_OBJECT('code','colorScheme','name','配色','type','enum','options',JSON_ARRAY('幻夜黑','星空蓝','远方紫'),'required',true)
) WHERE id = 15;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','ancNoise','name','主动降噪','type','boolean','required',true),
  JSON_OBJECT('code','batteryHours','name','续航','type','number','unit','小时','required',false)
) WHERE id = 16;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true),
  JSON_OBJECT('code','resolution','name','分辨率','type','enum','options',JSON_ARRAY('4K','8K'),'required',true),
  JSON_OBJECT('code','cameraEnabled','name','智慧摄像头','type','boolean','required',false)
) WHERE id = 18;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','screenInch','name','屏幕尺寸','type','number','unit','英寸','required',true),
  JSON_OBJECT('code','resolution','name','分辨率','type','enum','options',JSON_ARRAY('4K'),'required',true)
) WHERE id = 19;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','wifiStandard','name','WiFi 标准','type','enum','options',JSON_ARRAY('WiFi6','WiFi7'),'required',true),
  JSON_OBJECT('code','portCount','name','网口数量','type','integer','required',false),
  JSON_OBJECT('code','coverage','name','覆盖面积','type','number','unit','平方米','required',false)
) WHERE id = 21;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','powerW','name','额定功率','type','number','unit','W','required',false),
  JSON_OBJECT('code','stereoPair','name','支持立体声组','type','boolean','required',false)
) WHERE id = 22;
UPDATE crm_product_category SET attribute_template = JSON_ARRAY(
  JSON_OBJECT('code','unlockType','name','开锁方式','type','enum','options',JSON_ARRAY('指纹','人脸','密码','卡片'),'required',true),
  JSON_OBJECT('code','batteryMonths','name','电池续航','type','integer','unit','月','required',false)
) WHERE id = 23;

-- 几条示例产品，让"分类下有产品"的业务规则可以直接验证
INSERT INTO crm_product (category_id, code, name, specification, brand, color, base_unit, reference_price, source_type, attributes) VALUES
(2,  'HW-MATE60PRO', 'Mate 60 Pro',   '16GB+512GB，雅川青', '华为', '雅川青', '台', 6999.00, 2,
 JSON_OBJECT('storageGB','512GB','colorScheme','雅川青','screenInch',6.82,'satellite',true)),
(3,  'HW-PURA70',    'Pura 70',       '12GB+256GB，霜砂白', '华为', '霜砂白', '台', 5499.00, 2,
 JSON_OBJECT('storageGB','256GB','colorScheme','霜砂白','screenInch',6.6)),
(7,  'HW-BOOKXPRO',  'MateBook X Pro','Ultra9 32G 2T 微绒典藏版', '华为', NULL, '台', 14999.00, 2,
 JSON_OBJECT('cpuModel','酷睿 Ultra 9','ramGB',32,'storageTB',2.0,'screenInch',14.2,'weightKg',0.98)),
(16, 'HW-FREEBUDS6', 'FreeBuds 6',    '无限藕音版，星云灰', '华为', '星云灰', '个', 899.00, 2,
 JSON_OBJECT('ancNoise',true,'batteryHours',36.0)),
(21, 'HW-ROUTER7',   '凌霄 WiFi 7 路由器', 'BE3 Pro，双 2.5G 网口', '华为', NULL, '台', 499.00, 2,
 JSON_OBJECT('wifiStandard','WiFi7','portCount',4,'coverage',150.0));
