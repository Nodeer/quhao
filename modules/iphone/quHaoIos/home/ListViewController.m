//
//  HomeViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//


#import "ListViewController.h"
@interface ListViewController ()

@end
@implementation ListViewController

-(void)viewDidLoad
{
    [super viewDidLoad];
    self.title=@"商家列表";

    //添加的代码
    //添加上面的导航
    [self loadNavigationItem];
    
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
    
    _allCount = 0;
    _isLoading = NO;
    //注册
    self.tableView.frame = CGRectMake(0, 0, kDeviceWidth, kDeviceHeight);
    [self.tableView registerClass:[HomeCell class] forCellReuseIdentifier:@"ListCell"];
    
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    
    _isLoadOver = NO;
    [self createHud];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_tableFooterView == nil&& [_merchartsArray count]!=0) {
                if(_isLoadOver){
                    self.tableView.tableFooterView = nil;
                    _isLoading = YES;
                }else{
                    [self createFootView];
                    [self setFootState:PullRefreshNormal];
                }
            }
            [self.tableView reloadData];
            [_HUD hide:YES];
        });
    });
}

- (void)hudWasHidden:(MBProgressHUD *)hud
{
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

-(void)loadNavigationItem
{
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, 30, 30)];
    [btn addTarget:self action:@selector(clickSearch:) forControlEvents:UIControlEventTouchUpInside];
    [btn setImage:[UIImage imageNamed:@"searchWhite"] forState:UIControlStateNormal];
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithCustomView:btn];
    self.navigationItem.rightBarButtonItem = btnSearch;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)clickSearch:(id)sender
{
    SearchView * sView = [[SearchView alloc] init];
    sView.cityCode = self.cityCode;
    sView.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:sView animated:YES];
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
    cell.backgroundColor = [UIColor clearColor];
}

//设置行高
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
        static NSString *cellIdentify=@"ListCell";
        HomeCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
        //检查视图中有没闲置的单元格
        if(cell==nil){
            cell=[[HomeCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
        }
        cell.merchartModel=_merchartsArray[indexPath.row];
        [Helper arrowStyle:cell];
    
        return cell;
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSInteger row = [indexPath row];
    MerchartModel *n = [_merchartsArray objectAtIndex:row];
    if (n)
    {
        [self pushMerchartDetail:n andNavController:self.navigationController andIsNextPage:NO];
    }
    
}

//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage
{
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = model.id;
    mDetail.isNextPage = isNextPage;
    
    [navController pushViewController:mDetail animated:YES];
}


-(void)requestData
{
  if ([Helper isConnectionAvailable]){
      if (_isLoadOver) {
          return;
      }
      int pageIndex = _allCount/10+1;
      NSString *str1= [NSString stringWithFormat:@"%@%@%@&cityCode=%@&page=%d&userX=%f&userY=%f", IP,homeView_list_url,self.cateType,self.cityCode, pageIndex,self.longitude,self.latitude];
      NSString *response =[QuHaoUtil requestDb:str1];
      if([response isEqualToString:@""]){
          //异常处理
          [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
      }else{
          NSArray *jsonObjects=[QuHaoUtil analyseData:response];
          if(jsonObjects==nil){
              //解析错误
              [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
          }else{
              NSMutableArray *newMerc = [self addAfterInfo:jsonObjects];
              NSInteger count = [newMerc count];
              _allCount += count;
              if (count < 10)
              {
                  _isLoadOver = YES;
              }
              [_merchartsArray addObjectsFromArray:newMerc];
              
              //如果是第一页 则缓存下来
              if (_merchartsArray.count <= 10) {
                  [Helper saveCache:5 andID:1 andString:response];
              }
          }
          //[self doneLoadingTableViewData];
      }
      //[self.tableView reloadData];
  }
  else
  {
      _isLoadOver = YES;
      NSString *value = [Helper getCache:5 andID:1];
      if (value&&[_merchartsArray count]==0) {
          NSArray *jsonObjects=[QuHaoUtil analyseData:value];
          [_merchartsArray addObjectsFromArray:[self addAfterInfo:jsonObjects]];
          [self.tableView reloadData];
      }else{
          [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
      }
  }
}

//上拉刷新增加数据
-(NSMutableArray *)addAfterInfo:(NSArray *) objects
{
    NSMutableArray *news = [[NSMutableArray alloc] initWithCapacity:10];
    for(int i=0; i < [objects count];i++ ){
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name = [[objects objectAtIndex:i] objectForKey:@"name"];
        model.averageCost = [[[objects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id = [[objects objectAtIndex:i] objectForKey:@"id"];
        model.imgUrl = [[objects objectAtIndex:i] objectForKey:@"merchantImage"];
        model.enable = [[[objects objectAtIndex:i] objectForKey:@"enable"] boolValue];
        model.distance = [[objects objectAtIndex:i] objectForKey:@"distance"];
        model.youhuiExist = [[[objects objectAtIndex:i] objectForKey:@"youhuiExist"] boolValue];
        [news addObject:model];
    }
    
    return news;
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
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if(_isLoadOver){
                self.tableView.tableFooterView = nil;
                _isLoading = YES;
            }
            [self doneLoadingTableViewData];
            [self.tableView reloadData];
        });
    });
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

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
