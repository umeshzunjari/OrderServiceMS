package com.project.OrderServiceMS.service;


import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.OrderServiceMS.DTO.OrderDetailsDTO;
import com.project.OrderServiceMS.DTO.ProductsOrderedDTO;
import com.project.OrderServiceMS.entity.OrderDetailsEntity;
import com.project.OrderServiceMS.entity.ProductsOrderedEntity;
import com.project.OrderServiceMS.repository.OrderDetailsRepository;
import com.project.OrderServiceMS.repository.ProductsOrderedRepository;

@Service
public class OrderService {
	@Autowired
	private OrderDetailsRepository orderrepo;
	@Autowired
	private OrderDetailsEntity orderEntity;
	@Autowired
	private ProductsOrderedRepository orderProdsRepo;
	@Autowired
	private ProductsOrderedEntity productsOrdered;
	@Autowired
	public RestTemplate restTemplate;
	@Value("${userServiceUrl}")
	public String userServiceUrl;


		
		// This method is to view orders placed on products.(by buyerId)
		public ArrayList<OrderDetailsDTO> getAllOrders(Integer Buyerid){
			List<ProductsOrderedEntity> product = (List<ProductsOrderedEntity>) orderProdsRepo.findAll();
			ArrayList<OrderDetailsDTO> orders =new ArrayList<>();
			List<OrderDetailsEntity> ord= (List<OrderDetailsEntity>) orderrepo.findAll();
			for(OrderDetailsEntity ord1:ord) {
				if(ord1.getBuyerId().equals(Buyerid)) {
					ArrayList<ProductsOrderedDTO> orderedProducts =new ArrayList<>();
					for(ProductsOrderedEntity orderprod: product) {
						if(ord1.getOrderId().equals(orderprod.getOrderId())) {
							ProductsOrderedDTO prod =new ProductsOrderedDTO();
							prod.setOrderId(orderprod.getOrderId());
							prod.setPrice(orderprod.getPrice());
							prod.setProdId(orderprod.getProdId());
							prod.setQuantity(orderprod.getQuantity());
							prod.setSellerid(orderprod.getSellerid());
							prod.setStatus(orderprod.getStatus());
							orderedProducts.add(prod);
						}
						OrderDetailsDTO od= new OrderDetailsDTO();
						od.setAddress(ord1.getAddress());
						od.setAmount(ord1.getAmount());
						od.setBuyerId(ord1.getBuyerId()); 
						od.setDate(ord1.getDate());
						od.setOrderId(ord1.getOrderId());
						od.setStatus(ord1.getStatus());
						od.setOrderedProducts(orderedProducts);
						orders.add(od);
					}
				}
			}
			return orders;
		}
		
		public String deleteOrder(Integer orderId) {
			List<OrderDetailsEntity> ordersEntities=(List<OrderDetailsEntity>) orderrepo.findAll();
				// Deleting the order
			Integer sizeorder=ordersEntities.size();
				for(OrderDetailsEntity order: ordersEntities){
					if(order.getOrderId().equals(orderId)) {
						orderrepo.delete(order);					
					}
				}
				List<ProductsOrderedEntity> products=(List<ProductsOrderedEntity>) orderProdsRepo.findAll();
				Integer sizeproduct=products.size();
				// Deleting all the product ordered in that order
				for(ProductsOrderedEntity product:products) {
					if(product.getOrderId().equals(orderId)) {
							orderProdsRepo.delete(product);
					}}
				List<OrderDetailsEntity> orders=(List<OrderDetailsEntity>) orderrepo.findAll();
				List<ProductsOrderedEntity> prod=(List<ProductsOrderedEntity>) orderProdsRepo.findAll();
				
			if(sizeorder>orders.size()& sizeproduct>prod.size()){
				return "Order "+ orderId+ " is deleted successfully from the records";
			}else{
				return "Deletion cannot be done as the order with orderId "+orderId+ " does not exist";
			}
	}
		
	/*
	 * public void placeOrder(OrderDetailsDTO order) { ArrayList<ProductDTO>
	 * productsReceived=order.getOrderedProducts(); double amount=0d; for(ProductDTO
	 * product:productsReceived) { amount+=product.getPrice()*product.getQuantity();
	 * } Integer discount= calculateDiscount(order.getBuyerId());
	 * 
	 * String isPrivilegeUrl=userServiceUrl+"buyer/isPrivilege/"+order.getBuyerId();
	 * 
	 * ResponseEntity<Boolean> responseEntity1 =
	 * restTemplate.getForEntity(isPrivilegeUrl, Boolean.class);
	 * 
	 * Boolean isPrivileged=responseEntity1.getBody();
	 * 
	 * if(isPrivileged==true) { amount=amount-discount; }
	 * 
	 * order.setAmount(amount); order.setDate(new
	 * Date());order.setStatus("ORDER PLACED");
	 * 
	 * OrderEntity oe =new OrderEntity(); oe.setAddress(order.getAddress());
	 * oe.setAmount(amount); oe.setBuyerId(order.getBuyerId());
	 * oe.setDate(order.getDate()); oe.setOrderId(order.getOrderId());
	 * oe.setStatus(order.getStatus()); orderrepo.save(oe); }
	 */
	
	
		public String updateStatus(Integer orderId,Integer prodId,String status) {
			Boolean flag=false;
			try {
			List<ProductsOrderedEntity> products=(List<ProductsOrderedEntity>) orderProdsRepo.findAll();
			for(int i=0;i<products.size();i++){
				ProductsOrderedEntity product=products.get(i);
				// Checking the orderId and ProdId before updating
				if(product.getOrderId().equals(orderId) && product.getProdId().equals(prodId)) {
					BeanUtils.copyProperties(product,productsOrdered);
					orderProdsRepo.delete(product);
					productsOrdered.setStatus(status);
					orderProdsRepo.save(productsOrdered);
					flag=true;
				}
			}
			}catch(Exception e){
				e.printStackTrace();
				return "not able to update contact";
			}
			if(flag){
				return "Order status updated successfully";
			}else{
				return "some error occured.Check for issues";
			}
		}
}
	 
