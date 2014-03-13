//
//  HomeViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "HomeViewController.h"
#import "HomeCell.h"
#import "ASIHTTPRequest.h"
#import "SBJson.h"
#import "MerchartDetail.h"
#import "SearchView.h"
#import "QuHaoUtil.h"

@implementation ListViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    //添加的代码
    //添加上面的导航
    [self loadNavigationItem];

    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];

    _reloading = NO;
    _pageIndex=1;
    _whichView=1;
    //注册
    [self.tableView registerClass:[HomeCell class] forCellReuseIdentifier:@"ListCell"];
    if ([Helper isConnectionAvailable]){
        MBProgressHUD *HUD = [[MBProgressHUD alloc] initWithView:self.view];
        [self.view addSubview:HUD];
        //如果设置此属性则当前的view置于后台
        HUD.dimBackground = YES;
        //设置对话框文字
        HUD.labelText = @"正在加载...";
        //显示对话框
        [HUD showAnimated:YES whileExecutingBlock:^{
            //得到初始化页面的时间 下拉刷新取的是该时间以后的
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
            
            NSString *currentDateStr = [dateFormatter  stringFromDate:[NSDate date]];
            NSDate * dates=[dateFormatter dateFromString:currentDateStr] ;
            //NSDate * dates=[dateFormatter dateFromString:@"2012-08-01 12:22:33"] ;
            self.currentDateStr = [QuHaoUtil returnFormatString:[dateFormatter  stringFromDate:dates]];
            NSLog(@"==%@",self.currentDateStr);
            [self requestData:[NSString stringWithFormat:@"%@%@%@&sortBy=joinedDate",[Helper getIp],homeView_list_url,self.cateType] withPage:_pageIndex];
            [self.tableView reloadData];
        } completionBlock:^{
            //操作执行完后取消对话框
            [HUD removeFromSuperview];
            [self addHeader];
            [self addFooter];
        }];
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

-(void)loadNavigationItem
{
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 搜 索" rect:CGRectMake( 0, 7, 50, 35 )];
    [btnButton addTarget:self action:@selector(clickSearch:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)clickSearch:(id)sender
{
    SearchView * sView = [[SearchView alloc] init];
    sView.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:sView animated:YES];
}

//下拉刷新
- (void)addHeader
{
    MJRefreshHeaderView *header = [MJRefreshHeaderView header];
    header.scrollView = self.tableView;
    header.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        // 进入刷新状态就会回调这个Block
        
        _prevItemCount = [_merchartsArray count];
        _whichView=2;
        //page为0无效 暂时未做分页处理
        [self requestData:[NSString stringWithFormat:@"%@%@%@&date=%@",[Helper getIp],homeView_last_url,self.cateType,self.currentDateStr]
                 withPage:0];
        // 这里的refreshView其实就是header
        [self performSelector:@selector(doneWithView:) withObject:refreshView afterDelay:1.0];
    };
    
    _header = header;
}

//上拉加载更多
- (void)addFooter
{
    MJRefreshFooterView *footer = [MJRefreshFooterView footer];
    footer.scrollView = self.tableView;
    footer.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        _prevItemCount = [_merchartsArray count];
        ++_pageIndex;
        _whichView=1;
        [self requestData:[NSString stringWithFormat:@"%@%@%@",[Helper getIp],homeView_list_url,self.cateType]  withPage:_pageIndex];
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
        int row = [indexPath row];
        if (row >= [_merchartsArray count]) {
                [self performSelector:@selector(reload:)];
        }
        else {
            MerchartModel *n = [_merchartsArray objectAtIndex:row];
            if (n)
            {                   
                [self pushMerchartDetail:n andNavController:self.navigationController andIsNextPage:NO];
            }
        }
}

//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage
{
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = model.id;
    mDetail.isNextPage = isNextPage;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    
    [navController pushViewController:mDetail animated:YES];
}


-(void)requestData:(NSString *)urlStr withPage:(int)page
{
  _loadFlag=YES;
  if ([Helper isConnectionAvailable]){
      NSString *str1= [NSString stringWithFormat:@"%@&page=%d", urlStr, page];
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
              if(_whichView==1){
                  [self addAfterInfo:jsonObjects];
               }else{
                  [self addBeforeInfo:jsonObjects];
              }
              //如果是第一页 则缓存下来
              if (_merchartsArray.count <= 20) {
                  [Helper saveCache:5 andID:1 andString:response];
              }
          }
      }    
  }
  else
  {
      NSString *value = [Helper getCache:5 andID:1];
      if (value&&[_merchartsArray count]==0) {
          NSArray *jsonObjects=[QuHaoUtil analyseData:value];
          [self addAfterInfo:jsonObjects];
          [self.tableView reloadData];
      }else{
          _loadFlag = NO;
          [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
      }
  }
}

//上拉刷新增加数据
-(void)addAfterInfo:(NSArray *) objects
{
    for(int i=0; i < [objects count];i++ ){
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name=[[objects objectAtIndex:i] objectForKey:@"name"];
        model.averageCost=[[[objects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id=[[objects objectAtIndex:i] objectForKey:@"id"];
        model.imgUrl=[[objects objectAtIndex:i] objectForKey:@"merchantImage"];
        [_merchartsArray addObject:model];
    }
}

//下拉刷新增加数据
-(void)addBeforeInfo:(NSArray *) objects
{
    for(int i=0; i < [objects count];i++ ){
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name=[[objects objectAtIndex:i] objectForKey:@"name"];
        model.averageCost=[[[objects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id=[[objects objectAtIndex:i] objectForKey:@"id"];
        model.imgUrl=[[objects objectAtIndex:i] objectForKey:@"merchantImage"];
        
        [_merchartsArray insertObject:model atIndex:0];
    }
}

- (void)dealloc
{
    [_header free];
    [_footer free];
}

@end
