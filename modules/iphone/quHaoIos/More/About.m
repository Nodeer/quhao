//
//  About.m
//  quHaoApp
//
//  Created by sam on 13-9-30.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "About.h"

@implementation About


- (void)viewDidLoad
{
    [super viewDidLoad];
        
    self.navigationItem.title = @"关于我们";
    self.view.backgroundColor  = [ UIColor colorWithRed: 0.947
                                                                   green: 0.947
                                                                    blue: 0.947
                                                                   alpha: 1.0
                                                   ];
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIImageView *imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"logo"]];
    imageView.backgroundColor=[UIColor clearColor];
    imageView.frame = CGRectMake(120, 25, 80, 80);
    [self.view addSubview:imageView];
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(60, 110, 220, 30)];
    label.text = @"聪明的排队让生活更有效率！";
    label.font = [UIFont boldSystemFontOfSize:16];
    label.backgroundColor = [UIColor clearColor];
    [self.view addSubview:label];
    
    UITableView *tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 140, kDeviceWidth, 240) style:UITableViewStyleGrouped];
    tableView.delegate = self;
    tableView.dataSource = self;
    tableView.backgroundView = nil;
    tableView.backgroundColor = [UIColor clearColor];
    tableView.scrollEnabled = NO;
    [self.view addSubview:tableView];
    
#if IOS7_SDK_AVAILABLE
    if([tableView respondsToSelector:@selector(setSeparatorInset:)])
    {
        [tableView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    _version = [NSString stringWithFormat:@"最新版本: %@",@" "];
    if ([Helper isConnectionAvailable]){
        NSString *str1 = [NSString stringWithFormat:@"%@%@",IP,getLastestVersion];
        NSString *response = [QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSDictionary *jsonObjects = [QuHaoUtil analyseDataToDic:response];
            if(jsonObjects != nil){
                _version = [NSString stringWithFormat:@"最新版本: %@",[jsonObjects objectForKey:@"ios"]];
            }
        }
    }
    _arrayList = @[_version,@"客服电话: 18616580943",@"官方微博: 取号啦",@"网       址: www.quhao.la"];
    [tableView reloadData];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 4;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 50;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify=@"aboutCell";
    UITableViewCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
        cell.textLabel.textAlignment = NSTextAlignmentLeft;
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    }
    cell.textLabel.text =_arrayList[indexPath.row];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}
@end
