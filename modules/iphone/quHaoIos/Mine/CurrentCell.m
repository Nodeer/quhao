//
//  CurrentCell.m
//  quHaoIos
//
//  Created by sam on 14-3-13.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "CurrentCell.h"
#import "MerchartModel.h"

@implementation CurrentCell

-(id) initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self initSubviews];
        self.accessoryType=UITableViewCellAccessoryDetailDisclosureButton;
    }
    return self;
}

-(void)initSubviews
{
    
    self.egoImgView = [[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]];
    self.egoImgView.frame = CGRectMake(5, 10, 105, 80);
    [self.contentView addSubview:self.egoImgView];
    
    
    _titleLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.textColor=[UIColor blackColor];
    _titleLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 10, 200, 30);
    _titleLabel.font=[UIFont boldSystemFontOfSize:18];
    [self.contentView addSubview:_titleLabel];
    
//    _cancelButton=[UIButton buttonWithType:UIButtonTypeCustom];
//    _cancelButton.backgroundColor=[UIColor lightGrayColor];
//    _cancelButton.titleLabel.font = [UIFont boldSystemFontOfSize:13.0f];
//    [self.contentView addSubview:_cancelButton];
//    
    _cancelButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    _cancelButton.frame=CGRectMake(_titleLabel.frame.origin.x+5, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+6, 80, 35);
    [_cancelButton addTarget:self action:@selector(resetPass:) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:_cancelButton];
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    if ([[Helper returnUserString:@"showImage"] boolValue]&&![self.merchartModel.imgUrl isEqualToString:@""])
    {
        self.egoImgView.imageURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,[self.merchartModel.imgUrl  stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding ]]];
    }
    
    _titleLabel.text=self.merchartModel.name;
    
    [_cancelButton setTitle: @"取消号码" forState: UIControlStateNormal];
}

//- (void)clickCancel:(id)sender
//{
//     int i = [sender tag];
//    if([Helper isConnectionAvailable]){
//        NSString *url = [NSString stringWithFormat:@"%@%@?reservationId=%@",[Helper getIp],cancelQuhao, reservation.id];
//        NSString *response =[QuHaoUtil requestDb:url];
//        if(response){
//            [Helper showHUD2:@"已取消排队号码" andView:self.view andSize:100];
//            [self performSelector:@selector(clickToHome:) withObject:nil afterDelay:1.0f];
//        }else{
//            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
//        }
//    }else{
//        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
//    }
//}

-(void)dealloc
{
    _titleLabel=nil;
    _cancelButton=nil;
}
@end
