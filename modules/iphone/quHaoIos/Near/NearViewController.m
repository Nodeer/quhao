//
//  NearViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "NearViewController.h"
#define SearchKey @"60d9c5881dbe8dddd3de33aaacac0cbe"

@interface NearViewController ()

@property (nonatomic, strong) MAPointAnnotation *centerPointAnnotation;
@property (nonatomic) AMapSearchType searchType;

@end

@implementation NearViewController
@synthesize centerPointAnnotation = _centerPointAnnotation;
@synthesize searchType = _searchType;
@synthesize tableView;
- (id)init
{
    self = [super init];
    if (self)
    {
        [MAMapServices sharedServices].apiKey = SearchKey;
        self.searchType = AMapSearchType_PlaceAround;
        self.title=@"周边美食";
        self.tabBarItem.image = [UIImage imageNamed:@"near"];
        _isOpinion = NO;
        [self initMapView];
        _showList = 0;
        _dis = 3000;
        self.view.backgroundColor = [UIColor whiteColor];
    }
    
    self.tableView=[[UITableView alloc] initWithFrame:CGRectMake(0, 20, kDeviceWidth, kDeviceHeight-128)];
    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.delegate=self;
    self.tableView.dataSource=self;
    [self.view addSubview:self.tableView];

    
    return self;
}

- (void)initMapView
{
    self.ownMapView = [[MAMapView alloc] initWithFrame:self.view.bounds];
    self.ownMapView.visibleMapRect = MAMapRectMake(220880104, 101476980, 272496, 466656);
    
    self.search = [[AMapSearchAPI alloc] initWithSearchKey:[MAMapServices sharedServices].apiKey Delegate:self];
    self.search.delegate = self;
    
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
    
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    UIImage *backImage = [UIImage imageNamed:@"max_btn"];
    _button = [UIButton buttonWithType:UIButtonTypeCustom];
    _button.frame = CGRectMake(0, 0, kDeviceWidth, 20);
    [_button setBackgroundImage:backImage forState:UIControlStateNormal];
    _button.titleLabel.font = [UIFont boldSystemFontOfSize:13.0f];
    [_button setTitle:@"3千米" forState:UIControlStateNormal];
    [_button addTarget:self action:@selector(changeDis:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_button];

    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshed:) name:Notification_TabClick object:nil];
}

- (void)viewDidAppear:(BOOL)animated
{
    _reloading = NO;
    _pageIndex = 1;
    //注册
    if([Helper isConnectionAvailable]){
        if([_merchartsArray count]==0){
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            hud.labelText = NSLocalizedString(@"正在加载", nil);
            hud.square = YES;
            [self refreshAction];

            [hud hide:YES];
            [self addFooter];
        }
    }//如果没有网络连接
    else
    {
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

- (void)refreshed:(NSNotification *)notification
{
    if (notification.object) {
        if ([(NSString *)notification.object isEqualToString:@"1"]) {
            if (self.tableView.contentOffset.y == 0) {
                [self performSelector:@selector(refreshData:) withObject:nil afterDelay:0.5];
            }else{
                [self.tableView setContentOffset:CGPointZero animated:YES];
            }
        }
    }
}

//滚动条到达顶部时候刷新
- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView
{
        [self performSelector:@selector(refreshData:) withObject:nil afterDelay:0.5];
}

- (void)refreshData:(id)sender
{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = NSLocalizedString(@"正在刷新", nil);
    hud.square = YES;
    [_merchartsArray removeAllObjects];
    _pageIndex = 1;
    [self refreshAction];
    [hud hide:YES];
}

//上拉加载更多
- (void)addFooter
{
    MJRefreshFooterView *footer = [MJRefreshFooterView footer];
    footer.scrollView = self.tableView;
    footer.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        _prevItemCount = [_merchartsArray count];
        ++_pageIndex;
        [self refreshAction];
        [self performSelector:@selector(doneWithView:) withObject:refreshView afterDelay:1.0];
        
    };
    _footer = footer;
}

- (void)doneWithView:(MJRefreshBaseView *)refreshView
{
    // 刷新表格
    [self.tableView reloadData];
    // 调用endRefreshing结束刷新状态
    [refreshView endRefreshing];
}

#pragma mark tableviewdelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_merchartsArray count]==0) {
        self.tableView.separatorStyle = NO;
    }else{
        self.tableView.separatorStyle = YES;
    }
    return [_merchartsArray count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify=@"NearCell";
    NearCell *cell=[self.tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[NearCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
        self.tableView.separatorStyle =UITableViewCellSeparatorStyleSingleLine;
    }
    cell.merchartModel=_merchartsArray[indexPath.row];
    [Helper arrowStyle:cell];

    return cell;
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    if (row >= [_merchartsArray count]) {
        [self performSelector:@selector(reload:)];
    }
    else {
        MerchartModel *n = [_merchartsArray objectAtIndex:row];
        if (n&&n.id!=nil)
        {
            [self pushMerchartDetail:n andNavController:self.navigationController];
        }else{
            //当前只提交数据库一次
            if (!_isOpinion) {
                NSString *url = [NSString stringWithFormat:@"%@%@%@",[Helper getIp],updateGaodeToMerchant,n.pguid];
                NSString *response =[QuHaoUtil requestDb:url];
                if(![response isEqualToString:@""]){
                    [Helper showHUD2:@"暂时未开放！" andView:self.view andSize:100];
                }
                _isOpinion=YES;
            }else{
                
                [Helper showHUD2:@"暂时未开放！" andView:self.view andSize:100];
            }
        }
    }
}

#pragma mark  高德地图的方法
- (void)poiRequestCoordinate:(CLLocationCoordinate2D)coordinate
{
    AMapPlaceSearchRequest *request = [[AMapPlaceSearchRequest alloc] init];
    
    request.searchType = AMapSearchType_PlaceAround;
    
    request.location = [AMapGeoPoint locationWithLatitude:coordinate.latitude longitude:coordinate.longitude];
    request.keywords = @"餐饮";
    if(_dis != 0){
        request.radius = _dis;
    }
    request.page = _pageIndex;
    request.sortrule = 1;
    request.offset=10;
    //返回扩展信息
    //request.requireExtension = YES;
    
    [self.search AMapPlaceSearch:request];
}

#pragma mark - AMapSearchDelegate

/* POI 搜索回调. */
- (void)onPlaceSearchDone:(AMapPlaceSearchRequest *)request response:(AMapPlaceSearchResponse *)respons
{
    if (request.searchType != self.searchType)
    {
        return;
    }
    if (respons.pois.count == 0)
    {
        return;
    }
    
    [respons.pois enumerateObjectsUsingBlock:^(AMapPOI *obj, NSUInteger idx, BOOL *stop) {

        //[poiAnnotations addObject:[[POIAnnotation alloc] initWithPOI:obj]];
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name=obj.name;
        if (obj.distance<=1000) {
            model.distance=[NSString stringWithFormat:@"%dm",obj.distance];
        } else {
            float dis=obj.distance/1000;
            model.distance=[NSString stringWithFormat:@"%.1fkm",dis];
        }
        model.pguid=obj.uid;
        model.imgUrl=@"";
        NSString * result=[self getMerchart:obj.uid ];
        if(![result isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            model.id=result;
        }
        [_merchartsArray addObject:model];
        
    }];
    
    //NSLog(@"_merchartsArray===%d",[_merchartsArray count]);
    _prevItemCount = [_merchartsArray count];
    
    [self.tableView reloadData];
}

#pragma mark - Action Handle
- (void)refreshAction
{
    //测试用
    self.ownMapView.userLocation.coordinate= CLLocationCoordinate2DMake(31.138869,121.40948);
    self.centerPointAnnotation = [[MAPointAnnotation alloc] init];
    self.centerPointAnnotation.coordinate = self.ownMapView.userLocation.coordinate;
    self.centerPointAnnotation.title = @"我的位置";
    //[self.ownMapView addAnnotation:self.centerPointAnnotation];
    //NSLog(@"%f",self.centerPointAnnotation.coordinate.latitude);
    //NSLog(@"%f",self.centerPointAnnotation.coordinate.longitude);

    [self poiRequestCoordinate:self.centerPointAnnotation.coordinate];
}

-(NSString *)getMerchart:(NSString *)poiId
{
    NSString *url = [NSString stringWithFormat:@"%@%@%@",[Helper getIp],queryMerchantByPoiId,poiId];
    NSString *response =[QuHaoUtil requestDb:url];
    
    return response;
}

//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController
{
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = model.id;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    mDetail.hidesBottomBarWhenPushed=YES;
    [navController pushViewController:mDetail animated:YES];
}

- (void)viewDidDisappear:(BOOL)animated
{
    self.ownMapView.showsUserLocation = NO;
    self.ownMapView.userTrackingMode  = MAUserTrackingModeNone;
    
    [super viewDidDisappear:animated];
}

#pragma mark 下拉选择代码开始
-(void)showPopUpwithOption:(NSArray*)option
{
    _selectList = [[selectListView alloc] initWithOption:option];
    _selectList.delegate = self;
    [_selectList showInView:self.view animated:YES];
}

- (void)changeDis:(id)sender {
    if (_showList == 1) {//如果下拉框已显示，什么都不做
        return;
    }else {
        [_selectList fadeOut];
        _showList = 1;
        arryList = @[@"1千米",@"3千米",@"5千米",@"10千米",@"全城"];
        arryValueList = @[@"1000",@"3000",@"5000",@"100000",@"0"];
        [self showPopUpwithOption:arryList];
    }
}

- (void)selectListView:(selectListView *)listView didSelectedIndex:(NSInteger)anIndex{
    [_button setTitle:[arryList objectAtIndex:anIndex] forState:UIControlStateNormal];
    _dis = [[arryValueList objectAtIndex:anIndex] intValue];
    _showList = 0;

    if (self.tableView.contentOffset.y == 0) {
        [self performSelector:@selector(refreshData:) withObject:nil afterDelay:0.5];
    }else{
        [self.tableView setContentOffset:CGPointZero animated:YES];
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch  locationInView:self.view];
    if (point.y >200) {
        [_selectList fadeOut];
        _showList = 0;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)delloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
@end

