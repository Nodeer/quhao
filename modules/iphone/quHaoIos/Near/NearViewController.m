//
//  NearViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "NearViewController.h"
#define SearchKey @"60d9c5881dbe8dddd3de33aaacac0cbe"
#define pageSize 20
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
    
    self.tableView=[[UITableView alloc] initWithFrame:CGRectMake(0, 25, kDeviceWidth, kDeviceHeight-133)];
    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.delegate=self;
    self.tableView.dataSource=self;
    self.tableView.indicatorStyle=UIScrollViewIndicatorStyleWhite;

    [self.view addSubview:self.tableView];
    
    return self;
}

- (void)initMapView
{
    self.ownMapView = [[MAMapView alloc] initWithFrame:self.view.bounds];
    self.ownMapView.userTrackingMode = MAUserTrackingModeNone;
    self.ownMapView.visibleMapRect = MAMapRectMake(220880104, 101476980, 272496, 466656);
    self.ownMapView.delegate = self;
    self.search = [[AMapSearchAPI alloc] initWithSearchKey:[MAMapServices sharedServices].apiKey Delegate:self];
    self.search.delegate = self;
    
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
    _isLoading = NO;
    _isLoadOver = NO;
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.navigationController.navigationBar.translucent = NO;
    self.tabBarController.tabBar.translucent = NO;
    self.extendedLayoutIncludesOpaqueBars = NO;
#endif
    UIImage *backImage = [UIImage imageNamed:@"max_btn"];
    _button = [UIButton buttonWithType:UIButtonTypeCustom];
    _button.frame = CGRectMake(0, 0, kDeviceWidth, 25);
    [_button setBackgroundImage:backImage forState:UIControlStateNormal];
    _button.titleLabel.font = [UIFont boldSystemFontOfSize:13.0f];
    [_button setTitle:@"3千米" forState:UIControlStateNormal];
    [_button addTarget:self action:@selector(changeDis:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_button];
    _isFirst = YES;
    _isRefreshLoading = YES;
    _pageIndex = 1;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshedNear:) name:Notification_TabClick object:nil];
}

- (void)viewDidAppear:(BOOL)animated
{
    if(_isFirst){
        [self checkDw];
        _isRefreshLoading = YES;
    }
}

- (void)checkDw
{
    _isFirst = NO;
    if ([CLLocationManager locationServicesEnabled] &&([CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorized
                                                       || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined))
    {
        self.ownMapView.showsUserLocation = YES;
        _isMapLoading = 0;
    }else if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请在系统设置中开启定位服务" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(![Helper isConnectionAvailable]){
        [self createHud];
        _HUD.labelText = @"当前网络不可用";
        [_HUD hide:YES];
        return;
    }
}

-(void)mapView:(MAMapView*)mapView didUpdateUserLocation:(MAUserLocation*)userLocation
updatingLocation:(BOOL)updatingLocation
{
    self.ownMapView.showsUserLocation = NO;
    _isMapLoading++;
    if(_isMapLoading == 1){
        _latitude = userLocation.coordinate.latitude;
        _longitude = userLocation.coordinate.longitude;
        if([_merchartsArray count]==0){
            [self createHud];
            [self refreshAction];
        }
    }
}

-(void)mapView:(MAMapView*)mapView didFailToLocateUserWithError:(NSError*)error
{
    if(_HUD!=nil){
        [_HUD hide:YES];
    }
    _isMapLoading++;
    self.ownMapView.showsUserLocation = NO;
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"定位失败" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
    [alert show];
    return;
}

#pragma mark HUD
- (void)hudWasHidden:(MBProgressHUD *)hud {
    [_HUD removeFromSuperview];
	_HUD = nil;
}

-(void)createHud
{
    _HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:_HUD];
    _HUD.mode = MBProgressHUDModeIndeterminate;
    _HUD.labelText = @"正在加载";
    [_HUD show:YES];
    _HUD.delegate = self;
}

- (void)refreshedNear:(NSNotification *)notification
{
    if (notification.object) {
        if ([(NSString *)notification.object isEqualToString:@"1"]) {
            if(_isRefreshLoading){
                _isRefreshLoading = NO;
                [self checkDw];
                [self createHud];
                _HUD.labelText = @"正在刷新";
                
                if (self.tableView.contentOffset.y == 0) {
                    [self performSelector:@selector(refreshData:) withObject:nil afterDelay:0.5];
                }else{
                    [self.tableView setContentOffset:CGPointZero animated:YES];
                }
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
    [_merchartsArray removeAllObjects];
    _pageIndex = 1;
    [self refreshAction];
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
    MerchartModel *n = [_merchartsArray objectAtIndex:row];
    if (n&&n.id!=nil&&![n.id isEqualToString:@""])
    {
        [self pushMerchartDetail:n andNavController:self.navigationController];
    }else{
        //当前只提交数据库一次
        if (!_isOpinion) {
            NSString *url = [NSString stringWithFormat:@"%@%@%@",IP,updateGaodeToMerchant,n.pguid];
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
    request.offset = pageSize;
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
    int count = respons.pois.count;

    if (count == 0)
    {
        _isLoadOver = YES;
        return;
    }
    
    if (count < pageSize)
    {
        _isLoadOver = YES;
    }

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        AMapPOI *obj = nil;
        for (int i=0; i<count; i++) {
            obj = [respons.pois objectAtIndex:i];
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
            MerchartModel * temp=[self getMerchart:obj.uid ];
            model.id=temp.id;
            model.enable=temp.enable;
            [_merchartsArray addObject:model];
        }
      dispatch_async(dispatch_get_main_queue(), ^{
          if (_tableFooterView == nil) {
              if(_isLoadOver){
                  self.tableView.tableFooterView = nil;
                  _isLoading = YES;
              }else{
                  [self createFootView];
              }
          }
          if(_HUD!=nil){
              [_HUD hide:YES];
          }
          [self doneLoadingTableViewData];
          [self.tableView reloadData];
    });
    });
    _isRefreshLoading = YES;
}

- (void)search:(id)searchRequest error:(NSString*)errInfo
{
    //NSLog(@"==============%@",errInfo);
}

#pragma mark - Action Handle
- (void)refreshAction
{
    if (_isLoadOver) {
        return;
    }
    self.centerPointAnnotation = [[MAPointAnnotation alloc] init];
    self.centerPointAnnotation.coordinate = CLLocationCoordinate2DMake(_latitude, _longitude);
    self.centerPointAnnotation.title = @"我的位置";

    [self poiRequestCoordinate:self.centerPointAnnotation.coordinate];
}

-(MerchartModel *)getMerchart:(NSString *)poiId
{
    NSString *url = [NSString stringWithFormat:@"%@%@%@",IP,queryMerchantByPoiId,poiId];
    NSString *response =[QuHaoUtil requestDb:url];
    MerchartModel *model = [[MerchartModel alloc]init];
    if(![response isEqualToString:@""]){
        NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
        if(jsonObjects!=nil){
            model.enable = [jsonObjects  objectForKey:@"enable"];
            model.id = [jsonObjects objectForKey:@"id"];
        }
    }
    return model;
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
    _isRefreshLoading = NO;
    [self checkDw];
    [self createHud];
    _HUD.labelText = @"正在刷新";
    if (self.tableView.contentOffset.y == 0) {
        [self performSelector:@selector(refreshData:) withObject:nil afterDelay:0.5];
    }else{
        [self.tableView setContentOffset:CGPointZero animated:YES];
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch  locationInView:self.view];
    if (point.y >264) {
        [_selectList fadeOut];
        _showList = 0;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
    if(_HUD!=nil){
        [_HUD hide:YES];
    }
}

#pragma 上提刷新
-(void)createFootView
{
    self.tableView.tableFooterView = nil;
    _tableFooterView = [[UIView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, kDeviceWidth, 40.0f)];
    _loadMoreText = [[UILabel alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 116.0f, 40.0f)];
    [_loadMoreText setCenter:_tableFooterView.center];
    [_loadMoreText setFont:[UIFont fontWithName:@"Helvetica Neue" size:14]];
    [_loadMoreText setText:@" 上拉显示更多 "];
    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickMoreLable:)];
    _loadMoreText.userInteractionEnabled=YES;
    [_loadMoreText addGestureRecognizer:tapGesture];
    [_tableFooterView addSubview:_loadMoreText];
    
    _tableFooterActivityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(75.0f, 10.0f, 20.0f, 20.0f)];
    [_tableFooterActivityIndicator setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleGray];
    [_tableFooterActivityIndicator stopAnimating];
    [_tableFooterView addSubview:_tableFooterActivityIndicator];
    self.tableView.tableFooterView = _tableFooterView;
    _state = PullRefreshNormal;
}

-(void)onClickMoreLable:(id)sender
{
    if(_state == PullRefreshNormal){
        _isLoading = YES;
        [self setFootState:PullRefreshLoading];
        [self loadMore];
    }
}

-(void)loadMore
{
        _pageIndex++;
        [self refreshAction];
}

- (void)doneLoadingTableViewData
{
    [self refreshScrollViewDataSourceDidFinishedLoading:self.tableView];
    _isLoading = NO;
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if(scrollView.contentOffset.y + (scrollView.frame.size.height) > scrollView.contentSize.height && !_isLoading &&(_state == PullRefreshNormal))
    {
        _isLoading = YES;
		[self setFootState:PullRefreshLoading];
        [self loadMore];
    }
}

//增加footView状态
- (void)setFootState:(RefreshState)aState{
	
	switch (aState) {
		case PullRefreshNormal:
            _loadMoreText.text = @" 上拉显示更多 ";
			[_tableFooterActivityIndicator stopAnimating];
			break;
		case PullRefreshLoading:
            _loadMoreText.text = @" 正在加载,请稍后";
			[_tableFooterActivityIndicator startAnimating];
			break;
		default:
			break;
	}
	_state = aState;
}

- (void)refreshScrollViewDataSourceDidFinishedLoading:(UIScrollView *)scrollView {
	
    //	[UIView beginAnimations:nil context:NULL];
    //	[UIView setAnimationDuration:.3];
    //	[scrollView setContentInset:UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f)];
    //	[UIView commitAnimations];
	[self setFootState:PullRefreshNormal];
}

- (void)delloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
@end

