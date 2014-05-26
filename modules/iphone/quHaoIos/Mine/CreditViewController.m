//
//  CreditViewController.m
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CreditViewController.h"

@interface CreditViewController ()

@end

@implementation CreditViewController

-(void)loadNavigationItem
{   
    self.tabBarItem.title=@"积分消费情况";
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    _backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = _backButtonItem;
    
    UIButton *btnButton = [Helper getBtn:@" 编 辑" rect:CGRectMake( 0, 0, 40, 25 )];
    [btnButton addTarget:self action:@selector(remove:) forControlEvents:UIControlEventTouchUpInside];
    _editItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = _editItem;
}
- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (void)remove:(id)sender
{
    self.tableView.allowsMultipleSelectionDuringEditing = YES;
    [self.tableView setEditing:YES animated:YES];
    [self updateBarButtons];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //添加上面的导航
    [self loadNavigationItem];
    
    _mutiButton = [Helper getBtn:@"全部删除" rect:CGRectMake( 0, 0, 60, 25 )];
    [_mutiButton addTarget:self action:@selector(multiDeleteClicked:) forControlEvents:UIControlEventTouchUpInside];
    _multiDeleteBarButton = [[UIBarButtonItem alloc] initWithCustomView:_mutiButton];
    
    UIButton *cancelButton = [Helper getBtn:@"取 消" rect:CGRectMake( 0, 0, 40, 25 )];
    [cancelButton addTarget:self action:@selector(cancelButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    _cancelBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:cancelButton];
    
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    _creditArray = [[NSMutableArray alloc] initWithCapacity:0];
    _delArray = [[NSMutableArray alloc] initWithCapacity:0];
    [self createHud];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData:[NSString stringWithFormat:@"%@%@?accountId=%@",IP,credit_url,self.accouId]];
        dispatch_async(dispatch_get_main_queue(), ^{
            if([_creditArray count]!=0){
                [self.tableView reloadData];
                [_HUD hide:YES];
            }else{
                _HUD.labelText = @"暂无积分消费信息";
                [_HUD hide:YES afterDelay:1];
            }
        });
    });
}

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

//设置行高
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 70;
}

// 选中行
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self updateDeleteButtonTitle];
}

- (void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Update the delete button's title based on how many items are selected.
    [self updateDeleteButtonTitle];
}

// 是否可编辑
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    return YES;
}

//编辑模式
- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return UITableViewCellEditingStyleDelete;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_creditArray count]==0) {
        self.tableView.separatorStyle = NO;
    }else{
        self.tableView.separatorStyle = YES;
    }
    return [_creditArray count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify=@"creditCell";
    CreditCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[CreditCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
       //[cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    cell.creditModel=_creditArray[indexPath.row];
    
    return cell;
}

-(void)requestData:(NSString *)urlStr
{
    if ([Helper isConnectionAvailable]){
        NSString *str1= [NSString stringWithFormat:@"%@", urlStr];
        NSString *response =[QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            _HUD.labelText = @"网络异常,请稍后再试";
            [_HUD hide:YES];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                _HUD.labelText = @"网络异常,请稍后再试";
                [_HUD hide:YES];
            }else{
                Credit *model = nil;
                int i ;
                for(i=0; i < [jsonObjects count];i++ ){
                    model=[[Credit alloc]init];
                    model.merchantName = [[jsonObjects objectAtIndex:i] objectForKey:@"merchantName"];
                    model.merchantId = [[jsonObjects objectAtIndex:i] objectForKey:@"merchantId"];
                    model.status = [[jsonObjects objectAtIndex:i] objectForKey:@"status"];
                    model.seatNumber = [[jsonObjects objectAtIndex:i] objectForKey:@"seatNumber"];
                    model.myNumber = [[jsonObjects objectAtIndex:i] objectForKey:@"myNumber"];
                    model.jifen = fabs([[[jsonObjects objectAtIndex:i] objectForKey:@"jifen"] integerValue]);
                    model.reservationId = [[jsonObjects objectAtIndex:i] objectForKey:@"reservationId"];
                    model.cost = [[[jsonObjects objectAtIndex:i] objectForKey:@"cost"] boolValue];
                    model.created = [[jsonObjects objectAtIndex:i] objectForKey:@"created"];
                    model.id = [[jsonObjects objectAtIndex:i] objectForKey:@"id"];
                    [_creditArray addObject:model];
                }
            }
        }
    }//如果没有网络连接
    else
    {
        _HUD.labelText = @"当前网络不可用";
        [_HUD hide:YES];
    }
}

// 更新导航栏按钮
-(void) updateBarButtons
{
    // 如果是允许多选的状态，即进入批量删除模式
    if (self.tableView.allowsSelectionDuringEditing == YES) {
        //更新删除按钮
        [self updateDeleteButtonTitle];
        self.navigationItem.leftBarButtonItems = nil;
        self.navigationItem.leftBarButtonItem = _multiDeleteBarButton;
        self.navigationItem.rightBarButtonItem =_cancelBarButtonItem;
        
        return;
    }
    if (self.tableView.editing == NO) {// 如果是编辑状态，且不属于批量删除状态
        self.navigationItem.leftBarButtonItem = _backButtonItem;
        self.navigationItem.rightBarButtonItem = _editItem;
    }
}

// 更新删除按钮的标题
-(void)updateDeleteButtonTitle
{
    NSArray *selectedRows = [self.tableView indexPathsForSelectedRows];//得到选中行
    BOOL allItemsAreSelected = selectedRows.count == _creditArray.count;// 是否全选
    BOOL noItemsAreSelected = selectedRows.count == 0;// 选中行数是否为零
    if (allItemsAreSelected || noItemsAreSelected)
    {// 如果是全选或者未选，则删除键为删除全部
        [_mutiButton setTitle:@"全部删除" forState:UIControlStateNormal];
    }
    else
    {// 否则 删除键为删除（选中行数量）
        [_mutiButton setTitle:[NSString stringWithFormat:@"删除 (%ld)", (long)selectedRows.count] forState:UIControlStateNormal];
    }
}

- (void)multiDeleteClicked:(id)sender {

    NSArray *selectedRows = [self.tableView indexPathsForSelectedRows];
    BOOL deleteSpecificRows = selectedRows.count > 0;
    Credit * credit = nil;
    [_delArray removeAllObjects];
    // 删除特定的行
    if (deleteSpecificRows)
    {
        // 将所选的行的索引值放在一个集合中进行批量删除
        NSMutableIndexSet *indicesOfItemsToDelete = [NSMutableIndexSet new];
        for (NSIndexPath *selectionIndex in selectedRows)
        {
            credit = (Credit *)_creditArray[selectionIndex.row];
            [indicesOfItemsToDelete addIndex:selectionIndex.row];
            [_delArray addObject: credit.id];
        }
        if([_delArray count] !=0 ){
            //逻辑删除
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                NSString *str1= [NSString stringWithFormat:@"%@%@%@",IP,delCredit,[_delArray componentsJoinedByString:@","]];
                NSString *response =[QuHaoUtil requestDb:str1];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    if([response isEqualToString:@""]){
                        //异常处理
                        [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
                    }else{
                        [_creditArray removeObjectsAtIndexes:indicesOfItemsToDelete];
                        [self.tableView deleteRowsAtIndexPaths:selectedRows withRowAnimation:UITableViewRowAnimationFade];
                    }
                });
            });
        }
    }
    else
    {
        for (int i=0 ;i< [_creditArray count]; i++)
        {
            credit = (Credit *)_creditArray[i];
            [_delArray addObject: credit.id];
        }
        if([_delArray count] !=0 ){
            //逻辑删除
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                NSString *str1= [NSString stringWithFormat:@"%@%@%@",IP,delCredit,[_delArray componentsJoinedByString:@","]];
                NSString *response =[QuHaoUtil requestDb:str1];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    if([response isEqualToString:@""]){
                        //异常处理
                        [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
                    }else{
                        [_creditArray removeAllObjects];
                        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationTop];
                    }
                });
            });
        }
    }
    
    [self.tableView setEditing:NO animated:YES];
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    [self updateBarButtons];
}

// 取消按钮
- (void)cancelButtonClicked:(id)sender {
    
    [self.tableView setEditing:NO animated:YES];
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    [self updateBarButtons];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
