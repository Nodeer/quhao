//
//  NearCell.m
//  quHaoIos
//
//  Created by sam on 14-2-5.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "NearCell.h"

@implementation NearCell


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
    
    self.egoImgView = [[EGOImageView alloc] initWithFrame:CGRectMake(5, 10, 105, 80)];
    self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
    [self.contentView addSubview:self.egoImgView];
    
    
    _titleLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.font=[UIFont boldSystemFontOfSize:18];
    [self.contentView addSubview:_titleLabel];
    
    
    _disLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _disLabel.backgroundColor=[UIColor clearColor];
    [self.contentView addSubview:_disLabel];
    
    _statusLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _statusLabel.backgroundColor=[UIColor clearColor];
    [self.contentView addSubview:_statusLabel];
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    if (![[Helper returnUserString:@"showImage"] boolValue]||[self.merchartModel.imgUrl isEqualToString:@""])
    {
        self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
    }
    else
    {
        self.egoImgView.imageURL = [NSURL URLWithString:self.merchartModel.imgUrl];
    }
    
    _titleLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 15, 200, 30);
    _titleLabel.text=self.merchartModel.name;
    
    _statusLabel.frame=CGRectMake(_titleLabel.frame.origin.x, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+10, 130, 35);
    _statusLabel.textAlignment=NSTextAlignmentLeft;
    _statusLabel.font=[UIFont boldSystemFontOfSize:13];
    if(self.merchartModel.id!=nil){
        _statusLabel.text=@"可以在线取号";
    }else{
        _statusLabel.text=@"暂时不能取号";
    }
    
    _disLabel.frame=CGRectMake(_titleLabel.frame.origin.x+130, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+10, 50, 35);
    _disLabel.text=self.merchartModel.distance;
    _disLabel.textAlignment=NSTextAlignmentRight;
    _disLabel.font=[UIFont boldSystemFontOfSize:13];
}

-(void)dealloc
{
    _titleLabel=nil;
    _disLabel=nil;
    _statusLabel=nil;
}
@end
